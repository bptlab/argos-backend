package de.hpi.bpt.argos.persistence.model.parsing;

import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.common.parsing.XMLFileParserImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductConfiguration;
import de.hpi.bpt.argos.persistence.model.product.ProductConfigurationImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCause;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCauseImpl;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorTypeImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class parses backbone data from XML files.
 */
public class BackboneDataParserImpl extends XMLFileParserImpl {
	// these variables are in the order of the XML file, the order is important!
	protected static final String PRODUCTS_ELEMENT = "products";
	protected static final String PRODUCT_ELEMENT = "product";
	protected static final String PRODUCT_IDENTIFIER_ELEMENT = "productIdentifier";
	protected static final String PRODUCT_DESCRIPTION_ELEMENT = "productDescription";
	protected static final String PRODUCT_CONFIGURATION_ELEMENT = "productConfiguration";
	protected static final String CODING_PLUG_ELEMENT = "codingPlug";
	protected static final String CODING_PLUG_SOFTWARE_VERSION_ELEMENT = "codingPlugSoftwareVersion";
	protected static final String DISPLAY_CODE_ELEMENT = "displayCode";
	protected static final String CAUSE_CODE_ELEMENT = "causeCode";
	protected static final String ERROR_DESCRIPTION_ELEMENT = "errorDescription";
	protected static final String CAUSE_DESCRIPTION_ELEMENT = "causeDescription";
	protected static final String CAUSE_PREDICTION_ELEMENT = "causePrediction";

	protected static final String PRODUCT_FAMILY_SEPARATOR = "-";
	protected static final int PRODUCT_FAMILY_SPLIT_LENGTH = 2;
	protected static final double TO_PERCENT = 1.0 / 100.0;

	protected List<Long> cachedExternalProductIdentifiers = new ArrayList<>();
	protected String tempCurrentProductExternalId = "";
	protected String tempCurrentProductFamilyId = "";
	protected String tempCurrentCauseDescription = "";
	protected String tempCurrentProductName = "";
	protected String tempCurrentCauseCode = "";
	protected String tempCurrentErrorDisplayCode = "";
	protected String tempCurrentCodingPlug = "";
	protected Product currentProduct = null;
	protected ErrorType currentErrorType = null;
	protected ProductConfiguration currentProductConfiguration = null;

	// statistics
	protected String fileName;
	protected Date importStartTime;
	protected long productsImported = 0;
	protected long productConfigurationsImported = 0;
	protected long errorTypesImported = 0;
	protected long errorCausesImported = 0;

	// time conversion
	protected static final int SECONDS_PER_MINUTE = 60;
	protected static final int MINUTES_PER_HOUR = 60;
	protected static final int HOURS_PER_DAY = 24;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(File dataFile) {
		fileName = dataFile.getName();
		importStartTime = new Date();

		List<ProductFamily> families = entityManager.getProductFamilies();
		cachedExternalProductIdentifiers.clear();

		for (ProductFamily family : families) {
			for (Product product : family.getProducts()) {
				cachedExternalProductIdentifiers.add(product.getOrderNumber());
			}
		}

		super.parse(dataFile);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void endElement(String elementName) {
		super.startElement(elementName);

		switch (elementName) {
			case PRODUCT_ELEMENT:
				saveCurrentProduct();
				break;

			case PRODUCT_CONFIGURATION_ELEMENT:
				currentProductConfiguration = null;
				break;

			case PRODUCTS_ELEMENT:
				logStatistics();
				break;

			default:
				// empty, since nothing to do
				break;
		}

		if (elementName.equals(PRODUCT_ELEMENT)) {
			saveCurrentProduct();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void element(String elementData) {
		switch (latestOpenedElement(0)) {
			case PRODUCT_IDENTIFIER_ELEMENT:
				tempCurrentProductExternalId = elementData;
				break;

			case PRODUCT_DESCRIPTION_ELEMENT:
				startNewProduct(elementData);
				break;

			case CODING_PLUG_ELEMENT:
				tempCurrentCodingPlug = elementData;
				break;

			case CODING_PLUG_SOFTWARE_VERSION_ELEMENT:
				startNewProductConfiguration(elementData);
				break;

			case DISPLAY_CODE_ELEMENT:
				tempCurrentErrorDisplayCode = elementData;
				break;

			case CAUSE_CODE_ELEMENT:
				tempCurrentCauseCode = elementData;
				break;

			case ERROR_DESCRIPTION_ELEMENT:
				startNewErrorType(elementData);
				break;

			case CAUSE_DESCRIPTION_ELEMENT:
				tempCurrentCauseDescription = elementData;
				break;

			case CAUSE_PREDICTION_ELEMENT:
				startNewErrorCause(elementData);
				break;

			default:
				// empty, since nothing to do
				break;
		}
	}

	/**
	 * This method starts a new product and saves it in the database.
	 * @param productDescription - the product description, which should contain a product family id and a product name
	 */
	protected void startNewProduct(String productDescription) {

		long externalProductId;

		try {
			externalProductId = Long.parseLong(tempCurrentProductExternalId);
		} catch (Exception e) {
			logger.debug(String.format("can not parse product identifier '%1$s'", tempCurrentProductExternalId));
			logTrace(e);
			resetCurrentEntities();
			return;
		}

		if (!splitProductDescription(productDescription)
				|| cachedExternalProductIdentifiers.contains(externalProductId)) {
			resetCurrentEntities();
			return;
		}

		currentProduct = entityManager.getProduct(tempCurrentProductFamilyId, externalProductId);
		currentProduct.setName(tempCurrentProductName);
		productsImported++;
	}

	/**
	 * This method splits a product description and sets the current productFamilyId and the currentProductName.
	 * @param productDescription - the description for the current product
	 * @return - true if the description is valid
	 */
	protected boolean splitProductDescription(String productDescription) {

		String[] split = productDescription.split(PRODUCT_FAMILY_SEPARATOR);

		if (split.length < PRODUCT_FAMILY_SPLIT_LENGTH) {
			logger.info(String.format("product '%1$s' is not supported", productDescription));
			return false;
		}

		StringBuilder productName = new StringBuilder();
		productName.append(split[1]);

		// add separator to product name, if more than two split parts exist
		for (int i = PRODUCT_FAMILY_SPLIT_LENGTH; i < split.length; i++) {
			productName.append(PRODUCT_FAMILY_SEPARATOR);
			productName.append(split[i]);
		}

		tempCurrentProductFamilyId = split[0];
		tempCurrentProductName = productName.toString();
		return true;
	}

	/**
	 * This method starts a new product configuration and adds it to the current product.
	 * @param codingPlugSoftwareVersion - the coding plug software version of the new configuration
	 */
	protected void startNewProductConfiguration(String codingPlugSoftwareVersion) {

		int codingPlugId;
		float version;

		try {
			version = Float.parseFloat(codingPlugSoftwareVersion);
		} catch (Exception e) {
			logger.debug(String.format("can not parse coding plug software version '%1$s'", codingPlugSoftwareVersion));
			logTrace(e);
			return;
		}

		try {
			codingPlugId = Integer.parseInt(tempCurrentCodingPlug);
		} catch (Exception e) {
			logger.debug(String.format("can not parse coding plug id '%1$s'", tempCurrentCodingPlug));
			logTrace(e);
			return;
		}

		if (currentProductConfiguration != null) {
			currentProductConfiguration.addCodingPlugSoftwareVersion(version);
			return;
		}

		if (currentProduct == null) {
			return;
		}

		currentProductConfiguration = new ProductConfigurationImpl();
		currentProductConfiguration.setProduct(currentProduct);
		currentProductConfiguration.setCodingPlugId(codingPlugId);
		currentProductConfiguration.addCodingPlugSoftwareVersion(version);
		currentProduct.addProductConfiguration(currentProductConfiguration);
		productConfigurationsImported++;
	}

	/**
	 * This method starts a new errorEventType and adds it to the current product.
	 * @param errorDescription - the error description for the current error type
	 */
	protected void startNewErrorType(String errorDescription) {

		if (currentProductConfiguration == null) {
			return;
		}

		int causeCode;

		try {
			causeCode = Integer.parseInt(tempCurrentCauseCode);
		} catch (Exception e) {
			logger.debug(String.format("can not parse cause code '%1$s'", tempCurrentCauseCode));
			logTrace(e);
			currentErrorType = null;
			return;
		}

		currentErrorType = new ErrorTypeImpl();
		currentErrorType.setCauseCode(causeCode);
		currentErrorType.setDisplayCode(tempCurrentErrorDisplayCode);
		currentErrorType.setErrorDescription(errorDescription);

		if (currentProductConfigurationHasErrorType(currentErrorType)) {
			currentErrorType = null;
			return;
		}

		currentProductConfiguration.addErrorType(currentErrorType);
		errorTypesImported++;
	}

	/**
	 * This method starts a new errorCause and adds it to the current errorEventType.
	 * @param causePrediction - the error cause prediction as string
	 */
	protected void startNewErrorCause(String causePrediction) {

		if (currentProductConfiguration == null
				|| currentErrorType == null) {
			return;
		}

		double prediction;

		try {
			prediction = Double.parseDouble(causePrediction);

			if (prediction > 1.0) {
				prediction *= TO_PERCENT;
			}
		} catch (Exception e) {
			logger.debug(String.format("can not parse cause prediction '%1$s'", causePrediction));
			logTrace(e);
			return;
		}

		ErrorCause cause = new ErrorCauseImpl();
		cause.setDescription(tempCurrentCauseDescription);
		cause.setErrorPrediction(prediction);
		currentErrorType.addErrorCause(cause);

		errorCausesImported++;
	}

	/**
	 * This method saves the finished product in the database.
	 */
	protected void saveCurrentProduct() {

		if (currentProduct == null) {
			return;
		}

		logger.info(String.format("added product '%1$s' with '%2$d' configurations in family '%3$s'", currentProduct.getName(), currentProduct
				.getProductConfigurations().size(), currentProduct.getProductFamily().getName()));
		entityManager.updateEntity(currentProduct, ProductEndpoint.getProductUri(currentProduct.getId()));

		resetCurrentEntities();
	}

	/**
	 * This method resets all temporary variables.
	 */
	protected void resetCurrentEntities() {

		tempCurrentProductExternalId = "";
		tempCurrentProductFamilyId = "";
		tempCurrentCauseDescription = "";
		tempCurrentProductName = "";
		tempCurrentCauseCode = "";
		tempCurrentErrorDisplayCode = "";
		tempCurrentCodingPlug = "";
		currentProduct = null;
		currentErrorType = null;
		currentProductConfiguration = null;
	}

	/**
	 * This method checks, whether a error type is already present in the current product.
	 * @param errorType - the error type to check
	 * @return - true, if the error type is already present
	 */
	protected boolean currentProductConfigurationHasErrorType(ErrorType errorType) {
		if (currentProductConfiguration == null) {
			return false;
		}

		for (ErrorType type : currentProductConfiguration.getErrorTypes()) {
			if (type.getErrorTypeId().equalsIgnoreCase(errorType.getErrorTypeId())) {
				return true;
			}
		}

		return false;
	}

    /**
     * Logs an exception on log level trace.
     * @param e - exception to log
     */
	private void logTrace(Exception e) {
		logger.trace("Reason: ", e);
    }

	/**
	 * This method logs the statistics about the imported entities.
	 */
	protected void logStatistics() {
		logger.info(String.format("finished importing data from '%1$s' in '%2$s'. "
				+ "imported %3$d products, "
				+ "%4$d productConfigurations, "
				+ "%5$d errorTypes and "
				+ "%6$d errorCauses.",
				fileName,
				getTimeSinceStart(),
				productsImported,
				productConfigurationsImported,
				errorTypesImported,
				errorCausesImported));
	}

	/**
	 * This method calculates the passed time since the import process started and returns it as a string.
	 * @return - the passed time since start of the import process as string
	 */
	protected String getTimeSinceStart() {

		long passedTimeInSeconds = ((new Date()).getTime() - importStartTime.getTime()) / 1000;
		long[] passedTime = new long[] { 0, 0, 0, 0 };

		if (passedTimeInSeconds >= SECONDS_PER_MINUTE) {
			passedTime[3] = passedTimeInSeconds % SECONDS_PER_MINUTE;
		} else {
			passedTime[3] = passedTimeInSeconds;
		}

		long passedTimeInMinutes = passedTimeInSeconds / SECONDS_PER_MINUTE;

		if (passedTimeInMinutes >= MINUTES_PER_HOUR) {
			passedTime[2] = passedTimeInMinutes % MINUTES_PER_HOUR;
		} else {
			passedTime[2] = passedTimeInMinutes;
		}

		long passedTimeInHours = passedTimeInMinutes / MINUTES_PER_HOUR;

		if (passedTimeInHours >= HOURS_PER_DAY) {
			passedTime[1] = passedTimeInHours % HOURS_PER_DAY;
		} else {
			passedTime[1] = passedTimeInHours;
		}

		long passedTimeInDays = passedTimeInHours / HOURS_PER_DAY;
		passedTime[0] = passedTimeInDays;

		return String.format(
				"%d days, %d hours, %d minutes, %d seconds",
				passedTime[0],
				passedTime[1],
				passedTime[2],
				passedTime[3]);
	}
}
