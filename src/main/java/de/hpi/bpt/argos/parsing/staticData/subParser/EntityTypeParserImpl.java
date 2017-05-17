package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.XMLSubParserImpl;
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
public class EntityTypeParserImpl extends XMLSubParserImpl implements EntityTypeParser {

	private static final String NAME_ELEMENT = "name";
	private static final String ATTRIBUTE_ELEMENT = "attribute";

	private EntityTypeList entityTypes;
	private EntityType parentEntityType;
	private EntityType entityType;
	private Map<String, TypeAttribute> entityTypeAttributes;

	/**
	 * This constructor sets the parent parser.
	 * @param parent - the parent parser to be set
	 * @param entityTypes - the list of all entityTypes parsed so far
	 * @param parentType - the parent entityType of the entityType which will be parsed by this parser
	 */
	public EntityTypeParserImpl(XMLFileParser parent, EntityTypeList entityTypes, EntityType parentType) {
		super(parent);
		this.entityTypes = entityTypes;
		parentEntityType = parentType;
		entityTypeAttributes = new HashMap<>();

		entityType = new EntityTypeImpl();
		entityType.setParentId(parentType.getId());

		PersistenceAdapterImpl.getInstance().saveArtifacts(entityType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(String element) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementValue(String element, String value) {
		if (element.equalsIgnoreCase(NAME_ELEMENT) && getParent().latestOpenedElement(1).equalsIgnoreCase(ATTRIBUTE_ELEMENT)) {
			if (entityTypeAttributes.containsKey(value)) {
				return;
			}

			entityTypeAttributes.put(value, createTypeAttribute(value));
		} else if (element.equalsIgnoreCase(NAME_ELEMENT)) {
			entityType.setName(value);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String element) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getParentEntityType() {
		return parentEntityType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityType getEntityType() {
		return entityType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		entityTypes.add(entityType, new ArrayList<>(entityTypeAttributes.values()), parentEntityType);
	}

	/**
	 * This method creates a new typeAttribute for this entityType.
	 * @param name - the name of the typeAttribute to create
	 * @return - the newly created typeAttribute
	 */
	private TypeAttribute createTypeAttribute(String name) {
		TypeAttribute newTypeAttribute = new TypeAttributeImpl();

		newTypeAttribute.setTypeId(entityType.getId());
		newTypeAttribute.setName(name);

		return newTypeAttribute;
	}
}
