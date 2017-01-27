package de.hpi.bpt.argos.api.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import java.util.List;
import java.util.Map;

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

		JsonArray jsonProductFamilies = new JsonArray();

		for(ProductFamily family : productFamilies) {
			JsonObject jsonProductFamily = getProductFamilyBase(family);
			JsonArray jsonProducts = new JsonArray();

			for(Product product : family.getProducts()) {
				jsonProducts.add(getProductBase(product));
			}

			jsonProductFamily.add("products", jsonProducts);
			jsonProductFamilies.add(jsonProductFamily);
		}

		return serializer.toJson(jsonProductFamilies);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAllEventTypes(int productId) {
		Map<EventType, Integer> eventTypes = databaseConnection.listAllEventTypesForProduct(productId);

		JsonArray jsonEventTypes = new JsonArray();

		for(Map.Entry<EventType, Integer> eventTypeEntry : eventTypes.entrySet()) {
			JsonObject jsonEventType = getEventTypeBase(eventTypeEntry.getKey());
			jsonEventType.addProperty("numberOfEvents", eventTypeEntry.getValue());
			JsonArray jsonEventAttributes = new JsonArray();

			for(EventAttribute eventAttribute : eventTypeEntry.getKey().getAttributes()) {
				jsonEventAttributes.add(getEventAttribute(eventAttribute));
			}

			jsonEventType.add("attributes", jsonEventAttributes);
			jsonEventTypes.add(jsonEventType);
		}

		return serializer.toJson(jsonEventTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventsForProduct(int productId, int eventTypeId, int eventIndexFrom, int eventIndexTo) {
		List<Event> events = databaseConnection.listEventsForProductOfTypeInRange(productId, eventTypeId, eventIndexFrom,
				eventIndexTo);

		JsonArray jsonEvents = new JsonArray();

		for(Event event : events) {
			jsonEvents.add(getEvent(event));
		}

		return serializer.toJson(jsonEvents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDatabaseConnection(DatabaseConnection databaseConnection) {
		this.databaseConnection = databaseConnection;
	}

	/**
	 * This method returns a product family base (id, name, brand) as a JsonObject.
	 * @param productFamily - the product family which the base is required
	 * @return - a json representation of the product family's base
	 */
	protected JsonObject getProductFamilyBase(ProductFamily productFamily) {
		JsonObject jsonProductFamily = new JsonObject();
		jsonProductFamily.addProperty("id", productFamily.getId());
		jsonProductFamily.addProperty("name", productFamily.getName());
		jsonProductFamily.addProperty("brand", productFamily.getBrand());

		return jsonProductFamily;
	}

	/**
	 * This method returns a product base (id, name, numberOfDevices, numberOfEvents, productionStart, orderNumber, state, stateDescription) as a
	 * JsonObject.
	 * @param product - the product which base is required
	 * @return - a json representation of the product's base
	 */
	protected JsonObject getProductBase(Product product) {
		try {
			JsonObject jsonProduct = new JsonObject();
			jsonProduct.addProperty("id", product.getId());
			jsonProduct.addProperty("name", product.getName());
			jsonProduct.addProperty("numberOfDevices", product.getNumberOfDevices());
			jsonProduct.addProperty("numberOfEvents", product.getNumberOfEvents());
			jsonProduct.addProperty("productionStart", product.getProductionStart().toString());
			jsonProduct.addProperty("orderNumber", product.getOrderNumber());
			jsonProduct.addProperty("state", product.getState().toString());
			jsonProduct.addProperty("stateDescription", product.getStateDescription());

			return jsonProduct;
		} catch (Exception exception) {
			// TODO: log exception
			return new JsonObject();
		}
	}

	/**
	 * This method returns a event type base (id, name) as a JsonObject
	 * @param eventType - the event type which base is required
	 * @return - a json representation of the event type's base
	 */
	protected JsonObject getEventTypeBase(EventType eventType) {
		try {
			JsonObject jsonEventType = new JsonObject();
			jsonEventType.addProperty("id", eventType.getId());
			jsonEventType.addProperty("name", eventType.getName());

			return jsonEventType;
		} catch (Exception exception) {
			// TODO: log exception
			return new JsonObject();
		}
	}

	/**
	 * This method return a event attribute (id, name, type) as a JsonObject
	 * @param eventAttribute - the event attribute
	 * @return - a json representation of the event attribute
	 */
	protected JsonObject getEventAttribute(EventAttribute eventAttribute) {
		try {
			JsonObject jsonEventAttribute = new JsonObject();
			jsonEventAttribute.addProperty("id", eventAttribute.getId());
			jsonEventAttribute.addProperty("name", eventAttribute.getName());
			jsonEventAttribute.addProperty("type", eventAttribute.getType().toString());

			return jsonEventAttribute;
		} catch (Exception exception) {
			// TODO: log exception
			return new JsonObject();
		}
	}

	/**
	 * This method returns the json object for a given event.
	 * @param event - the event to convert to json
	 * @return - the json object for the event
	 */
	protected JsonObject getEvent(Event event) {
		try {
			JsonObject jsonEvent = new JsonObject();
			jsonEvent.addProperty("id", event.getId());

			for (EventData data : event.getEventData()) {
				switch (data.getEventAttribute().getType()) {
					case STRING: {
						jsonEvent.addProperty(data.getEventAttribute().getName(), data.getValue());
						break;
					}
					case INTEGER: {
						jsonEvent.addProperty(data.getEventAttribute().getName(), serializer.fromJson(data.getValue(), Integer.class));
						break;
					}
					case FLOAT: {
						jsonEvent.addProperty(data.getEventAttribute().getName(), serializer.fromJson(data.getValue(), Float.class));
						break;
					}
					case DATE: {
						jsonEvent.addProperty(data.getEventAttribute().getName(), data.getValue());
						break;
					}
					default: {
						jsonEvent.addProperty(data.getEventAttribute().getName(), data.getValue());
						break;
					}
				}
			}

			return jsonEvent;
		} catch (Exception exception) {
			// TODO: log exception
			return new JsonObject();
		}
	}
}
