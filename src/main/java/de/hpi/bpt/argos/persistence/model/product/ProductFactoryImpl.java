package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ProductFactoryImpl implements ProductFactory {

	protected DatabaseConnection databaseConnection;

	public ProductFactoryImpl(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Product getProduct(String productFamilyIdentifier, int productIdentifier) {
		Product product = databaseConnection.getProduct(productIdentifier);

		if (product == null) {
			ProductFamily productFamily = databaseConnection.getProductFamily(productFamilyIdentifier);

			if (productFamily == null) {
				productFamily = new ProductFamilyImpl();
				productFamily.setName(productFamilyIdentifier);
			}

			product = new ProductImpl();
			product.setOrderNumber(productIdentifier);

			productFamily.getProducts().add(product);

			List<ProductFamily> productFamilies = new ArrayList<>();
			productFamilies.add(productFamily);
			databaseConnection.saveProductFamilies(productFamilies);
		}

		return product;
	}
}
