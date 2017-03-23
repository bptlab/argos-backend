package de.hpi.bpt.argos.core;


import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.EventImpl;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttributeImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventData;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataImpl;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.event.type.EventTypeImpl;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;
import de.hpi.bpt.argos.persistence.model.product.ProductFamilyImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductImpl;
import de.hpi.bpt.argos.persistence.model.product.ProductState;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class ArgosTestUtil {

	private static final Random random = new Random();
	private static Argos argos;

	public static void setup(Argos argos) {
		ArgosTestUtil.argos = argos;
	}

	public static String getCurrentTimestamp() {
		return new SimpleDateFormat("dd_MM_yyyy_HH_mm_ss").format(new Date());
	}

	public static int getRandomInteger(int min, int max) {
		return random.nextInt(max) + min;
	}

	public static long getRandomLong() {
		return random.nextLong();
	}

	public static float getRandomFloat() {
		return random.nextFloat();
	}

	public static String getRandomString() {
		return UUID.randomUUID().toString();
	}

	public static ProductFamily createProductFamily() {
		ProductFamily newProductFamily = new ProductFamilyImpl();

		newProductFamily.setName("ProductFamily_" + getCurrentTimestamp());
		newProductFamily.setBrand("Brand_" + getRandomInteger(0, Integer.MAX_VALUE));

		argos.getPersistenceEntityManager().updateEntity(newProductFamily);

		return newProductFamily;
	}

	public static Product createProduct(ProductFamily productFamily) {
		Product newProduct = new ProductImpl();

		newProduct.setName("Product_" + getCurrentTimestamp());
		newProduct.setState(ProductState.RUNNING);
		newProduct.setStateDescription("This product is running smoothly");
		newProduct.setOrderNumber(getRandomLong());
		newProduct.setProductionStart(new Date());
		newProduct.getTransitionToRunningState().setQueryString("query to running");
		newProduct.getTransitionToRunningState().setUuid(getRandomString());
		newProduct.getTransitionToWarningState().setQueryString("query to warning");
		newProduct.getTransitionToWarningState().setUuid(getRandomString());
		newProduct.getTransitionToErrorState().setQueryString("query to error");
		newProduct.getTransitionToErrorState().setUuid(getRandomString());
		newProduct.setProductFamily(productFamily);
		productFamily.getProducts().add(newProduct);

		argos.getPersistenceEntityManager().updateEntity(newProduct);
		argos.getPersistenceEntityManager().updateEntity(productFamily);

		return newProduct;
	}

	public static EventType createEventType() {
		EventType newEventType = new EventTypeImpl();

		String eventTypeName = "EventType_" + getCurrentTimestamp();

		newEventType.setName(eventTypeName);
		newEventType.getEventQuery().setQueryString("event query");
		newEventType.getEventQuery().setUuid(getRandomString());

		EventAttribute timestamp = new EventAttributeImpl();
		timestamp.setName("timestamp_" + eventTypeName);
		timestamp.setType(EventDataType.DATE);

		newEventType.getAttributes().add(timestamp);
		newEventType.setTimestampAttribute(timestamp);

		EventAttribute productId = new EventAttributeImpl();
		productId.setName("productId");
		productId.setType(EventDataType.LONG);

		newEventType.getAttributes().add(productId);

		EventAttribute productFamilyId = new EventAttributeImpl();
		productFamilyId.setName("productFamilyId");
		productFamilyId.setType(EventDataType.STRING);

		newEventType.getAttributes().add(productFamilyId);

		argos.getPersistenceEntityManager().updateEntity(newEventType);

		return newEventType;
	}

	public static Event createEvent(EventType type, Product product) {
		Event newEvent = new EventImpl();

		newEvent.setProduct(product);
		product.incrementNumberOfEvents(1);

		newEvent.setEventType(type);
		generateEventData(newEvent);

		argos.getPersistenceEntityManager().updateEntity(newEvent);

		return newEvent;
	}

	private static void generateEventData(Event event) {
		for (EventAttribute attribute : event.getEventType().getAttributes()) {

			EventData data = new EventDataImpl();
			data.setEventAttribute(attribute);

			switch (attribute.getType()) {
				case DATE:
					data.setValue((new Date(getRandomLong())).toString());
					break;
				case INTEGER:
					data.setValue(String.valueOf(getRandomInteger(0, Integer.MAX_VALUE)));
					break;
				case LONG:
					data.setValue(String.valueOf(getRandomLong()));
					break;
				case FLOAT:
					data.setValue(String.valueOf(getRandomFloat()));
					break;
				case STRING:
					data.setValue(getRandomString());
					break;
				default:
					data.setValue("");
					break;
			}

			event.getEventData().add(data);
		}
	}
}
