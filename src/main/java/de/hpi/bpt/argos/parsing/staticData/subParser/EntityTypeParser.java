package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.staticData.ArtifactParserImpl;
import de.hpi.bpt.argos.parsing.staticData.EntityTypeList;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityTypeImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * {@inheritDoc}
 * This subParser is responsible for parsing entityTypes.
 */
public class EntityTypeParser extends ArtifactParserImpl<EntityType> {

	private static final String NAME_ELEMENT = "name";
	private static final String ATTRIBUTE_ELEMENT = "attribute";

	private Map<String, TypeAttribute> entityTypeAttributes;

	/**
	 * This constructor sets the parent parser.
	 * @param parent - the parent parser to be set
	 * @param entityTypes - the list of all entityTypes parsed so far
	 * @param parentType - the parent entityType of the entityType which will be parsed by this parser
	 */
	public EntityTypeParser(XMLFileParser parent, EntityTypeList entityTypes, EntityType parentType) {
		super(parent, entityTypes, parentType);
		entityTypeAttributes = new HashMap<>();

		artifact = new EntityTypeImpl();
		artifact.setParentId(parentType.getId());

		// add name to attribute list to make it accessible in mappings, ...
		entityTypeAttributes.put(NAME_ELEMENT, createTypeAttribute(NAME_ELEMENT));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(String element) {
		// not needed in this parser
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementValue(String element, String value) {
		if (element.equalsIgnoreCase(NAME_ELEMENT) && parentParser.latestOpenedElement(1).equalsIgnoreCase(ATTRIBUTE_ELEMENT)) {
			entityTypeAttributes.put(value, createTypeAttribute(value));

		} else if (element.equalsIgnoreCase(NAME_ELEMENT)) {
			artifact.setName(artifact.getName() + value);

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String element) {
		if (element.equalsIgnoreCase(NAME_ELEMENT) && !parentParser.latestOpenedElement(1).equalsIgnoreCase(ATTRIBUTE_ELEMENT)) {
			if (!entityTypes.nameInUse(artifact.getName())) {
				PersistenceAdapterImpl.getInstance().saveArtifacts(artifact);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		if (artifact.getId() == 0) {
			return;
		}
		entityTypes.add(artifact, new ArrayList<>(entityTypeAttributes.values()), parentArtifact);
	}

	/**
	 * This method creates a new typeAttribute for this entityType.
	 * @param name - the name of the typeAttribute to create
	 * @return - the newly created typeAttribute
	 */
	private TypeAttribute createTypeAttribute(String name) {
		TypeAttribute newTypeAttribute = new TypeAttributeImpl();

		newTypeAttribute.setTypeId(artifact.getId());
		newTypeAttribute.setName(name);

		return newTypeAttribute;
	}
}
