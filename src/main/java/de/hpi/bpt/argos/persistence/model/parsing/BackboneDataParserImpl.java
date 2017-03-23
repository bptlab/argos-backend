package de.hpi.bpt.argos.persistence.model.parsing;

import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.common.parsing.XMLFileParserImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCause;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCauseImpl;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorTypeImpl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses backbone data from XML files.
 */
public class BackboneDataParserImpl extends XMLFileParserImpl {
	// these variables are in the order of the XML file, the order is important!
	protected static final String PRODUCT_ELEMENT = "product";
	protected static final String PRODUCT_IDENTIFIER_ELEMENT = "productIdentifier";
	protected static final String PRODUCT_DESCRIPTION_ELEMENT = "productDescription";
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
	protected Product currentProduct = null;
	protected ErrorType currentErrorType = null;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void parse(File dataFile) {
		super.parse(dataFile);

		List<ProductFamily> families = entityManager.getProductFamilies();
		cachedExternalProductIdentifiers.clear();

		for (ProductFamily family : families) {
			for (Product product : family.getProducts()) {
				cachedExternalProductIdentifiers.add(product.getOrderNumber());
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void endElement(String elementName) {
		super.startElement(elementName);

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
			logger.error(String.format("can not parse product identifier '%1$s'", tempCurrentProductExternalId), e);
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
	}

	/**
	 * This method splits a product description and sets the current productFamilyId and the currentProductName.
	 * @param productDescription - the description for the current product
	 * @return - true if the description is valid
	 */
	protected boolean splitProductDescription(String productDescription) {

		String[] split = productDescription.split(PRODUCT_FAMILY_SEPARATOR);

		if (split.length != PRODUCT_FAMILY_SPLIT_LENGTH) {
			logger.info(String.format("product '%1$s' is not supported", productDescription));
			return false;
		}

		tempCurrentProductFamilyId = split[0];
		tempCurrentProductName = split[1];
		return true;
	}

	/**
	 * This method starts a new errorEventType and adds it to the current product.
	 * @param errorDescription - the error description for the current error type
	 */
	protected void startNewErrorType(String errorDescription) {

		if (currentProduct == null) {
			return;
		}

		int causeCode;

		try {
			causeCode = Integer.parseInt(tempCurrentCauseCode);
		} catch (Exception e) {
			logger.error(String.format("can not parse cause code '%1$s'", tempCurrentCauseCode), e);
			currentErrorType = null;
			return;
		}

		currentErrorType = new ErrorTypeImpl();
		currentErrorType.setCauseCode(causeCode);
		currentErrorType.setDisplayCode(tempCurrentErrorDisplayCode);
		currentErrorType.setErrorDescription(errorDescription);

		if (currentProductHasErrorType(currentErrorType)) {
			currentErrorType = null;
			return;
		}

		currentProduct.addErrorType(currentErrorType);
	}

	/**
	 * This method starts a new errorCause and adds it to the current errorEventType.
	 * @param causePrediction - the error cause prediction as string
	 */
	protected void startNewErrorCause(String causePrediction) {

		if (currentProduct == null
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
			logger.error(String.format("can not parse cause prediction '%1$s'", causePrediction), e);
			return;
		}

		ErrorCause cause = new ErrorCauseImpl();
		cause.setDescription(tempCurrentCauseDescription);
		cause.setErrorPrediction(prediction);
		currentErrorType.addErrorCause(cause);
	}

	/**
	 * This method saves the finished product in the database.
	 */
	protected void saveCurrentProduct() {

		if (currentProduct == null) {
			return;
		}

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
		currentProduct = null;
		currentErrorType = null;
	}

	/**
	 * This method checks, whether a error type is already present in the current product.
	 * @param errorType - the error type to check
	 * @return - true, if the error type is already present
	 */
	protected boolean currentProductHasErrorType(ErrorType errorType) {
		if (currentProduct == null) {
			return false;
		}

		for (ErrorType type : currentProduct.getErrorTypes()) {
			if (type.getErrorTypeId().equalsIgnoreCase(errorType.getErrorTypeId())) {
				return true;
			}
		}

		return false;
	}
}
