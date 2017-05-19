package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.staticData.ArtifactParserImpl;
import de.hpi.bpt.argos.parsing.staticData.EntityTypeList;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.Attribute;
import de.hpi.bpt.argos.storage.dataModel.attribute.AttributeImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.EntityImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.util.LoggerUtilImpl;
import de.hpi.bpt.argos.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * This subParser is responsible for parsing entityInstances.
 */
public class EntityInstanceParser extends ArtifactParserImpl<Entity> {

	private static final String NAME_ELEMENT = "name";
	private static final String TYPE_ELEMENT = "type";
	private static final String ATTRIBUTES_ELEMENT = "attributes";

	private Map<String, Attribute> entityAttributes;
	private Pair<EntityType, List<TypeAttribute>> entityType;

	/**
	 * This constructor sets all members according to the given values.
	 * @param parent - the parent parser to be set
	 * @param entityTypes - a list of all entityTypes, which are valid in the context of this parsing
	 * @param parentEntity - the parentEntity of the entity, which this parser is responsible for
	 */
	public EntityInstanceParser(XMLFileParser parent, EntityTypeList entityTypes, Entity parentEntity) {
		super(parent, entityTypes, parentEntity);
		entityAttributes = new HashMap<>();

		artifact = new EntityImpl();
		artifact.setParentId(parentEntity.getId());
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
		if (parentParser.latestOpenedElement(1).equalsIgnoreCase(ATTRIBUTES_ELEMENT)) {
			Attribute newAttribute = createAttribute(element, value);

			if (newAttribute == null) {
				return;
			}

			entityAttributes.put(element, newAttribute);

		} else if (element.equalsIgnoreCase(NAME_ELEMENT)) {
			artifact.setName(artifact.getName() + value);

		} else if (element.equalsIgnoreCase(TYPE_ELEMENT)) {
			try {
				entityType = entityTypes.get(value);
				artifact.setTypeId(entityType.getKey().getId());

				if (entityType.getKey().getId() != 0) {
					PersistenceAdapterImpl.getInstance().saveArtifacts(artifact);
				}

			} catch (Exception e) {
				LoggerUtilImpl.getInstance().error(logger, String.format("cannot find entity type '%1$s'", value), e);
			}

		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String element) {
		// not needed in this parser
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		if (entityType == null) {
			return;
		}

		// add the name attribute to the list of all attributes
		Attribute newAttribute = createAttribute(NAME_ELEMENT, artifact.getName());

		if (newAttribute == null) {
			return;
		}
		entityAttributes.put(NAME_ELEMENT, newAttribute);

		List<Attribute> attributes = new ArrayList<>(entityAttributes.values());
		parentParser.getArtifactBatch().add(artifact);
		parentParser.getArtifactBatch().add(attributes.toArray(new Attribute[attributes.size()]));
	}

	/**
	 * This method creates a new attribute for this entity.
	 * @param name - the name of the attribute to create
	 * @param value - the value for this entity
	 * @return - the newly created attribute, or null if something went wrong
	 */
	private Attribute createAttribute(String name, String value) {
		if (entityType == null) {
			return null;
		}

		TypeAttribute typeAttribute = null;

		for (TypeAttribute attribute : entityType.getValue()) {
			if (attribute.getName().equals(name)) {
				typeAttribute = attribute;
				break;
			}
		}

		if (typeAttribute == null) {
			return null;
		}

		Attribute newAttribute = new AttributeImpl();
		newAttribute.setOwnerId(artifact.getId());
		newAttribute.setTypeAttributeId(typeAttribute.getId());
		newAttribute.setValue(value);

		return newAttribute;
	}
}
