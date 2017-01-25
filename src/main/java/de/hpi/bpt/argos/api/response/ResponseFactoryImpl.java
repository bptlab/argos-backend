package de.hpi.bpt.argos.api.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ResponseFactoryImpl implements ResponseFactory {
	protected static final Gson serializer = new Gson();

	protected DatabaseConnection databaseConnection;

	public ResponseFactoryImpl(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAllProductFamilies() {
		List<ProductFamily> productFamilies = databaseConnection.listAllProductFamilies();

		List<JsonObject> jsonProductFamilies = new ArrayList<JsonObject>();

		for(ProductFamily family : productFamilies) {
			JsonObject jsonFamily = new JsonObject();
			jsonFamily.addProperty("id", family.getId());

			JsonObject familyMetaData = new JsonObject();
			familyMetaData.addProperty("name", family.getName());
			familyMetaData.addProperty("brand", family.getBrand());

			jsonFamily.add("metaData", familyMetaData);

			JsonArray jsonProducts = new JsonArray();

			for(Product product : family.getProducts()) {
				JsonObject jsonProduct = new JsonObject();
				jsonProduct.addProperty("id", product.getId());

				JsonObject productMetaData = new JsonObject();
				productMetaData.addProperty("name", product.getName());
				productMetaData.addProperty("numberOfDevices", product.getNumberOfDevices());
				productMetaData.addProperty("numberOfEvents", product.getNumberOfEvents());
				productMetaData.addProperty("productionStart", product.getProductionStart().toString());
				productMetaData.addProperty("orderNumber", product.getOrderNumber());
				productMetaData.addProperty("state", product.getState().toString());
				productMetaData.addProperty("stateDescription", product.getStateDescription());

				jsonProduct.add("metaData", productMetaData);

				jsonProducts.add(jsonProduct);
			}

			jsonFamily.add("products", jsonProducts);
			jsonProductFamilies.add(jsonFamily);
		}

		return serializer.toJson(jsonProductFamilies);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}
}
