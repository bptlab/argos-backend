package de.hpi.bpt.argos.eventHandling.schema;

import de.hpi.bpt.argos.persistence.model.event.attribute.EventAttribute;
import de.hpi.bpt.argos.persistence.model.event.data.EventDataType;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeSchemaGeneratorImpl implements EventTypeSchemaGenerator {

	protected static final String SCHEMA_EXTENSION = "xsd";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventTypeSchema(EventType eventType) {
		String schema = "";

		for (EventAttribute attribute : eventType.getAttributes()) {
			schema = appendElement(attribute.getName(), attribute.getType(), schema);
		}

		schema = extendWithSequence(schema);
		schema = extendWithComplexType(schema);
		schema = extendWithElement(eventType.getName(), schema);
		schema = extendWithHeader(eventType.getName(), schema);

		return schema;
	}

	/**
	 * This method returns the schema header with the schema name embedded.
	 * @param schemaName - the schema name to embed
	 * @param innerContent - the inner content of the event type schema
	 * @return - the schema header
	 */
	protected String extendWithHeader(String schemaName, String innerContent) {
		return String.format("<?xml version='1.0' encoding='utf-8'?>"
				+ "<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema' xmlns='%1$s.%2$s' "
				+ "targetNamespace='%1$s.%2$s' elementFormDefault='qualified'>"
				+ "%3$s</xs:schema>", schemaName, SCHEMA_EXTENSION, innerContent);
	}

	/**
	 * This method extends the current content with another schema element.
	 * @param elementName - the name of the new element
	 * @param innerContent - the current content
	 * @return - the new content
	 */
	protected String extendWithElement(String elementName, String innerContent) {
		return String.format("<xs:element name='%1$s'>%2$s</xs:element>", elementName, innerContent);
	}

	/**
	 * This method extends the current content with a new complex type.
	 * @param innerContent - the current content
	 * @return - the new content
	 */
	protected String extendWithComplexType(String innerContent) {
		return String.format("<xs:complexType>%1$s</xs:complexType>", innerContent);
	}

	/**
	 * This method extends the current content with a new sequence.
	 * @param innerContent - the current content
	 * @return - the new content
	 */
	protected String extendWithSequence(String innerContent) {
		return String.format("<xs:sequence>%1$s</xs:sequence>", innerContent);
	}

	/**
	 * This method appends the current content with a new element.
	 * @param elementName - the name of the new element
	 * @param elementType - the type of the new element
	 * @param innerContent - the current content
	 * @return - the new content
	 */
	protected String appendElement(String elementName, EventDataType elementType, String innerContent) {
		String typeString;

		switch (elementType) {
			case STRING:
				typeString = "xs:string";
				break;

			case INTEGER:
				typeString = "xs:int";
				break;

			case FLOAT:
				typeString = "xs:float";
				break;

			case DATE:
				typeString = "xs:date";
				break;

			default:
				typeString = "xs:string";
				break;

		}

		return String.format("%1$s<xs:element name='%2$s' type='%3$s'/>", innerContent, elementName, typeString);
	}
}
