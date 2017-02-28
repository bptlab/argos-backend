package de.hpi.bpt.argos.api.response;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import de.hpi.bpt.argos.api.eventType.EventTypeEndpoint;
import de.hpi.bpt.argos.api.product.ProductEndpoint;
import de.hpi.bpt.argos.eventHandling.EventPlatformRestEndpoint;
import de.hpi.bpt.argos.eventHandling.EventReceiver;
import de.hpi.bpt.argos.persistence.database.PersistenceEntityManager;
import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventQueryImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.HaltException;

import java.util.List;
import java.util.Map;

import static spark.Spark.halt;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ResponseFactoryImpl implements ResponseFactory {
	protected static final Logger logger = LoggerFactory.getLogger(ResponseFactoryImpl.class);
	protected static final Gson serializer = new Gson();
	protected static final JsonParser jsonParser = new JsonParser();

	protected static final String JSON_EVENT_QUERY_ATTRIBUTE = "eventQuery";
	protected static final String JSON_EVENT_TYPE_ATTRIBUTE = "eventType";

	protected PersistenceEntityManager entityManager;
	protected EventPlatformRestEndpoint eventPlatformRestEndpoint;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(PersistenceEntityManager entityManager, EventPlatformRestEndpoint eventPlatformRestEndpoint) {
		this.entityManager = entityManager;
		this.eventPlatformRestEndpoint = eventPlatformRestEndpoint;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAllProductFamilies() {
		List<ProductFamily> productFamilies = entityManager.getProductFamilies();

		JsonArray jsonProductFamilies = new JsonArray();

		for (ProductFamily family : productFamilies) {
			jsonProductFamilies.add(getProductFamily(family));
		}

		return serializer.toJson(jsonProductFamilies);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProductFamily(long productFamilyId) {
		ProductFamily productFamily = entityManager.getProductFamily(productFamilyId);

		if (productFamily == null) {
			halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "cannot find product family");
		}

		JsonObject jsonProductFamily = getProductFamilyBase(productFamily);

		return serializer.toJson(jsonProductFamily);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getProduct(long productId) {
		Product product = entityManager.getProduct(productId);

		if (product == null) {
			halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "cannot find product");
		}

		JsonObject jsonProduct = getProductBase(product);

		return serializer.toJson(jsonProduct);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAllEventTypes(long productId) {

		Product product = entityManager.getProduct(productId);

		if (product == null) {
			halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "cannot find product");
		}

		Map<EventType, Integer> eventTypes = entityManager.getEventTypes(productId);

		JsonArray jsonEventTypes = new JsonArray();

		for (Map.Entry<EventType, Integer> eventTypeEntry : eventTypes.entrySet()) {
			JsonObject jsonEventType = getEventType(eventTypeEntry.getKey());
			jsonEventType.addProperty("numberOfEvents", eventTypeEntry.getValue());

			jsonEventTypes.add(jsonEventType);
		}

		return serializer.toJson(jsonEventTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventType(long eventTypeId) {
		EventType eventType = entityManager.getEventType(eventTypeId);
		if (eventType == null) {
			halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "Event type not found");
		}
		return serializer.toJson(getEventType(eventType));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getAllEventTypes() {
		List<EventType> eventTypes = entityManager.getEventTypes();

		JsonArray jsonEventTypes = new JsonArray();

		for (EventType eventType : eventTypes) {
			jsonEventTypes.add(getEventType(eventType));
		}

		return serializer.toJson(jsonEventTypes);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventsForProduct(long productId, long eventTypeId, int eventIndexFrom, int eventIndexTo) {
		List<Event> events = entityManager.getEvents(productId, eventTypeId, eventIndexFrom, eventIndexTo);
		JsonArray jsonEvents = new JsonArray();

		for (Event event : events) {
			jsonEvents.add(getEvent(event));
		}

		return serializer.toJson(jsonEvents);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEvent(long eventId) {
		Event event = entityManager.getEvent(eventId);
		if (event == null) {
			halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "Event not found");
		}
		JsonObject jsonEvent = getEvent(event);
		return jsonEvent.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void createEventType(String requestBody) {
		try {
			JsonObject jsonBody = jsonParser.parse(requestBody).getAsJsonObject();

			String eventQuery = jsonBody.get(JSON_EVENT_QUERY_ATTRIBUTE).getAsString();
			JsonObject jsonEventType = jsonBody.get(JSON_EVENT_TYPE_ATTRIBUTE).getAsJsonObject();

			if (eventQuery == null || eventQuery.length() == 0) {
				halt(ResponseFactory.HTTP_ERROR_CODE, "no event query given in body");
			}

			if (jsonEventType == null) {
				halt(ResponseFactory.HTTP_ERROR_CODE, "no event type given in body");
			}

			EventType eventType = entityManager.createEventType(jsonEventType);

			if (eventType == null) {
				halt(ResponseFactory.HTTP_ERROR_CODE, "event type name already in use, or failed to parse event type");
			} else {

				if (eventType.getEventQuery() == null) {
					eventType.setEventQuery(new EventQueryImpl());
				}

				eventType.getEventQuery().setQueryString(eventQuery);

				if (!eventPlatformRestEndpoint.getEventSubscriber().registerEventQuery(eventType)) {
					halt(ResponseFactory.HTTP_ERROR_CODE, "cannot register event type");
				}

				entityManager.updateEntity(eventType, EventTypeEndpoint.getEventTypeUri(eventType.getId()));
			}

		} catch (HaltException halt) {
			logger.info(String.format("cannot create event type: %1$s -> %2$s", halt.statusCode(), halt.body()));
			throw halt;
		} catch (Exception e) {
			logger.error("cannot parse request body to event type '" + requestBody + "'", e);
			halt(ResponseFactory.HTTP_ERROR_CODE, e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateEventQuery(long eventTypeId, String requestBody) {
		try {
			JsonObject jsonBody = jsonParser.parse(requestBody).getAsJsonObject();

			String eventQuery = jsonBody.get(JSON_EVENT_QUERY_ATTRIBUTE).getAsString();

			if (eventQuery == null || eventQuery.length() == 0) {
				halt(ResponseFactory.HTTP_ERROR_CODE, "no event query given in body");
			}

			EventType eventType = entityManager.getEventType(eventTypeId);

			if (eventType == null) {
				halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "event type not found");
			} else {

				if (!eventType.isEditable()) {
					halt(ResponseFactory.HTTP_FORBIDDEN_CODE, "you must not edit this event type");
				}

				if (!eventPlatformRestEndpoint.getEventSubscriber().updateEventQuery(eventType, eventQuery)) {
					halt(ResponseFactory.HTTP_ERROR_CODE, "event platform did not accept the updated event query");
				}

				entityManager.updateEntity(eventType, EventTypeEndpoint.getEventTypeUri(eventType.getId()));
			}

		} catch (HaltException halt) {
			logger.info(String.format("cannot update event query: %1$s -> %2$s", halt.statusCode(), halt.body()));
			throw halt;
		} catch (Exception e) {
			logger.error("cannot parse request body to event query '" + requestBody + "'", e);
			halt(ResponseFactory.HTTP_ERROR_CODE, e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void updateStatusEventQuery(long productId, ProductState newState, String requestBody) {
		try {
			JsonObject jsonBody = jsonParser.parse(requestBody).getAsJsonObject();

			String eventQuery = jsonBody.get(JSON_EVENT_QUERY_ATTRIBUTE).getAsString();

			if (eventQuery == null || eventQuery.length() == 0) {
				halt(ResponseFactory.HTTP_ERROR_CODE, "no event query given in body");
			}

			Product product = entityManager.getProduct(productId);

			if (product == null) {
				halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "product not found");
			} else {

				if (product.getStatusUpdateQuery(newState) == null) {
					halt(ResponseFactory.HTTP_ERROR_CODE, "new state is not supported by this product");
				}

				if (!eventPlatformRestEndpoint.getEventSubscriber().updateEventQuery(
						product.getStatusUpdateQuery(newState),
						eventQuery,
						EventReceiver.getReceiveStatusUpdateEventUri(product.getOrderNumber(), newState))) {
					halt(ResponseFactory.HTTP_ERROR_CODE, "event platform did not accept the updated status query");
				}

				entityManager.updateEntity(product, ProductEndpoint.getProductUri(productId));
			}

		} catch (HaltException halt) {
			logger.info(String.format("cannot update status event query: %1$s -> %2$s", halt.statusCode(), halt.body()));
			throw halt;
		} catch (Exception e) {
			logger.error("cannot parse request body to status event query '" + requestBody + "'", e);
			halt(ResponseFactory.HTTP_ERROR_CODE, e.getMessage());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String deleteEventType(long eventTypeId) {
		JsonArray blockingEventTypeIds = new JsonArray();

		EventType eventType = entityManager.getEventType(eventTypeId);

		if (eventType == null) {
			halt(ResponseFactory.HTTP_NOT_FOUND_CODE, "cannot find event type");
		} else {

			if (!eventType.isDeletable()) {
				halt(ResponseFactory.HTTP_FORBIDDEN_CODE, "you must not delete this event type");
			}

			List<EventType> eventTypes = entityManager.getEventTypes();

			for (EventType type : eventTypes) {
				if (type.getId() == eventTypeId
						|| type.getEventQuery() == null
						|| type.getEventQuery().getQueryString() == null
						|| type.getEventQuery().getQueryString().length() == 0) {
					continue;
				}

				if (type.getEventQuery().getQueryString().contains(eventType.getName())) {
					blockingEventTypeIds.add(type.getId());
				}
			}

			if (blockingEventTypeIds.size() == 0) {

				eventPlatformRestEndpoint.getEventSubscriber().deleteEventType(eventType);

				return "";
			} else {
				return serializer.toJson(blockingEventTypeIds);
			}
		}

		return "";
	}

	/**
	 * This method returns a product family as a JsonObject.
	 * @param productFamily - the product family
	 * @return - a json representation of a product family
	 */
	protected JsonObject getProductFamily(ProductFamily productFamily) {
		JsonObject jsonProductFamily = getProductFamilyBase(productFamily);
		JsonArray jsonProducts = new JsonArray();

		for (Product product : productFamily.getProducts()) {
			jsonProducts.add(getProductBase(product));
		}

		jsonProductFamily.add("products", jsonProducts);
		return jsonProductFamily;
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
			jsonProduct.addProperty("productFamilyId", product.getProductFamily().getId());
			jsonProduct.addProperty("name", product.getName());
			jsonProduct.addProperty("numberOfDevices", product.getNumberOfDevices());
			jsonProduct.addProperty("numberOfEvents", product.getNumberOfEvents());
			jsonProduct.addProperty("productionStart", product.getProductionStart().toString());
			jsonProduct.addProperty("orderNumber", product.getOrderNumber());
			jsonProduct.addProperty("state", product.getState().toString());
			jsonProduct.addProperty("stateDescription", product.getStateDescription());

			JsonObject stateQueries = new JsonObject();
			for (ProductState state : ProductState.values()) {
				if (product.getStatusUpdateQuery(state) == null) {
					continue;
				}

				stateQueries.addProperty(state.toString(), product.getStatusUpdateQuery(state).getQueryString());
			}

			jsonProduct.add("statusUpdateQueries", stateQueries);

			return jsonProduct;
		} catch (Exception exception) {
			logger.error("Cannot parse product base", exception);
			return new JsonObject();
		}
	}

	/**
	 * This method returns an event type base as a JsonObject.
	 * @param eventType - the event type which base is required
	 * @return - a json representation of the event type's base
	 */
	protected JsonObject getEventTypeBase(EventType eventType) {
		try {
			JsonObject jsonEventType = new JsonObject();
			jsonEventType.addProperty("id", eventType.getId());
			jsonEventType.addProperty("name", eventType.getName());
			jsonEventType.addProperty("timestampAttributeName", eventType.getTimestampAttribute().getName());

			if (eventType.getName().equals(EventType.getStatusUpdateEventTypeName())) {
				jsonEventType.addProperty("eventQuery", "");
			} else {
				jsonEventType.addProperty("eventQuery", eventType.getEventQuery().getQueryString());
			}

			return jsonEventType;
		} catch (Exception exception) {
			logger.error("Cannot parse event type base", exception);
			return new JsonObject();
		}
	}

	/**
	 * This method returns an event type as a JsonObject.
	 * @param eventType - the event type
	 * @return - a json representation of the event type
	 */
	protected JsonObject getEventType(EventType eventType) {
		try {
			JsonObject jsonEventType = getEventTypeBase(eventType);

			JsonArray jsonEventAttributes = new JsonArray();

			for (EventAttribute eventAttribute : eventType.getAttributes()) {
				jsonEventAttributes.add(getEventAttribute(eventAttribute));
			}

			jsonEventType.add("attributes", jsonEventAttributes);

			return jsonEventType;
		} catch (Exception exception) {
			logger.error("cannot parse event type", exception);
			return new JsonObject();
		}
	}

	/**
	 * This method return a event attribute (id, name, type) as a JsonObject.
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
			logger.error("Cannot parse event attribute", exception);
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
					case STRING:
						jsonEvent.addProperty(data.getEventAttribute().getName(), data.getValue());
						break;

					case INTEGER:
						jsonEvent.addProperty(data.getEventAttribute().getName(), serializer.fromJson(data.getValue(), Integer.class));
						break;

					case FLOAT:
						jsonEvent.addProperty(data.getEventAttribute().getName(), serializer.fromJson(data.getValue(), Float.class));
						break;

					case DATE:
						jsonEvent.addProperty(data.getEventAttribute().getName(), data.getValue());
						break;

					default:
						jsonEvent.addProperty(data.getEventAttribute().getName(), data.getValue());
						break;
				}
			}

			return jsonEvent;
		} catch (Exception exception) {
			logger.error("Cannot parse event", exception);
			return new JsonObject();
		}
	}
}
