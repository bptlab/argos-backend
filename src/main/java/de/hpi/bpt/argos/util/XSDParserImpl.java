package de.hpi.bpt.argos.util;

import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.event.type.EventType;

import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class XSDParserImpl implements XSDParser {
	private static final String SCHEMA_EXTENSION = "xsd";

	private static XSDParser instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private XSDParserImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static XSDParser getInstance() {
		if (instance == null) {
			instance = new XSDParserImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getEventTypeSchema(EventType eventType, List<TypeAttribute> typeAttributes) {
		String schema = "";

		for (TypeAttribute typeAttribute : typeAttributes) {
			String elementType = "xs:string";

			if (typeAttribute.getId() == eventType.getTimeStampAttributeId()) {
				elementType = "xs:date";
			}

			schema = appendElement(typeAttribute.getName(), elementType, schema);
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
	private String extendWithHeader(String schemaName, String innerContent) {
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
	private String extendWithElement(String elementName, String innerContent) {
		return String.format("<xs:element name='%1$s'>%2$s</xs:element>", elementName, innerContent);
	}

	/**
	 * This method extends the current content with a new complex type.
	 * @param innerContent - the current content
	 * @return - the new content
	 */
	private String extendWithComplexType(String innerContent) {
		return String.format("<xs:complexType>%1$s</xs:complexType>", innerContent);
	}

	/**
	 * This method extends the current content with a new sequence.
	 * @param innerContent - the current content
	 * @return - the new content
	 */
	private String extendWithSequence(String innerContent) {
		return String.format("<xs:sequence>%1$s</xs:sequence>", innerContent);
	}

	/**
	 * This method appends the current content with a new element.
	 * @param elementName - the name of the new element
	 * @param elementType - the data type of this element
	 * @param innerContent - the current content
	 * @return - the new content
	 */
	private String appendElement(String elementName, String elementType, String innerContent) {
		return String.format("%1$s<xs:element name='%2$s' type='%3$s'/>", innerContent, elementName, elementType);
	}
}
