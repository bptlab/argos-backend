package de.hpi.bpt.argos.persistence.model.parsing;

import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.common.parsing.XMLFileParserImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCause;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorCauseImpl;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorType;
import de.hpi.bpt.argos.persistence.model.product.error.ErrorTypeImpl;

/**
 * This class parses backbone data from XML files.
 */
public class BackboneDataParserImpl extends XMLFileParserImpl {
	protected static final String PRODUCT_ELEMENT = "product";
	protected static final String PRODUCT_IDENTIFIER_ELEMENT = "productIdentifier";
	protected static final String PRODUCT_DESCRIPTION_ELEMENT = "productDescription";
	protected static final String CAUSE_CODE_ELEMENT = "causeCode";
	protected static final String CAUSE_DESCRIPTION_ELEMENT = "causeDescription";
	protected static final String CAUSE_PREDICTION_ELEMENT = "causePrediction";

	protected String tempCurrentProductExternalId = "";
	protected String tempCurrentProductFamilyId = "";
	protected String tempCurrentCauseDescription = "";
	protected String tempCurrentProductName = "";
	protected Product currentProduct = null;
	protected ErrorType currentErrorType = null;

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
			case CAUSE_CODE_ELEMENT:
				startNewErrorType(elementData);
				break;
			case CAUSE_DESCRIPTION_ELEMENT:
				tempCurrentCauseDescription = elementData;
				break;
			case CAUSE_PREDICTION_ELEMENT:
				startNewErrorCause(elementData);
				break;
		}
	}

	/**
	 * This method starts a new product and saves it in the database.
	 * @param productDescription - the product description, which should contain a product family id and a product name
	 */
	protected void startNewProduct(String productDescription) {

		int externalProductId;

		try {
			externalProductId = Integer.parseInt(tempCurrentProductExternalId);
		} catch (Exception e) {
			logger.error(String.format("can not parse product identifier '%1$s'", tempCurrentProductExternalId), e);
			resetCurrentEntities();
			return;
		}

		if (!splitProductDescription(productDescription)
				|| entityManager.getProduct(externalProductId) != null) {
			resetCurrentEntities();
			return;
		}

		currentProduct = entityManager.getProduct(tempCurrentProductFamilyId, externalProductId);
		currentProduct.setName(tempCurrentProductName);
	}

	/**
	 * This method splits a product description and sets the current productFamilyId and the currentProductName
	 * @param productDescription - the description for the current product
	 * @return - true if the description is valid
	 */
	protected boolean splitProductDescription(String productDescription) {

		String[] split = productDescription.split("-");

		if (split.length != 2) {
			logger.info(String.format("product '%1$s' is not supported", productDescription));
			return false;
		}

		tempCurrentProductFamilyId = split[0];
		tempCurrentProductName = split[1];
		return true;
	}

	/**
	 * This method starts a new errorEventType and adds it to the current product.
	 * @param causeCode - the cause code for this error type as string
	 */
	protected void startNewErrorType(String causeCode) {

		if (currentProduct == null) {
			return;
		}

		int code;

		try {
			code = Integer.parseInt(causeCode);
		} catch (Exception e) {
			logger.error(String.format("can not parse cause code '%1$s'", causeCode), e);
			currentErrorType = null;
			return;
		}

		currentErrorType = new ErrorTypeImpl();
		currentErrorType.setCauseCode(code);
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
				prediction /= 100.0;
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

		tempCurrentProductName = "";
		tempCurrentProductFamilyId = "";
		tempCurrentProductExternalId = "";
		tempCurrentCauseDescription = "";
		currentProduct = null;
		currentErrorType = null;
	}
}
