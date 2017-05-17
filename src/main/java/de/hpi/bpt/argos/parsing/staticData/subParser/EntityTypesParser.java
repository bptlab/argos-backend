package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.XMLSubParserImpl;
import de.hpi.bpt.argos.parsing.staticData.EntityTypeList;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.entity.type.VirtualRootType;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * {@inheritDoc}
 * This subParser is responsible for parsing entityTypes.
 */
public class EntityTypesParser extends XMLSubParserImpl {

	private static final String ENTITY_TYPES_ROOT_ELEMENT = "types";
	private static final String ENTITY_TYPE_ROOT_ELEMENT = "type";
	private static final String ENTITY_TYPE_CHILD_TYPES_ELEMENT = "childTypes";

	private Deque<EntityTypeParser> parserStack;
	private EntityTypeList entityTypes;

	/**
	 * This constructor sets the parent parser.
	 * @param parent - the parent parser to be set
	 */
	public EntityTypesParser(XMLFileParser parent, EntityTypeList entityTypes) {
		super(parent);
		parserStack = new ArrayDeque<>();
		this.entityTypes = entityTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(String element) {
		// new entityType?
		if (element.equalsIgnoreCase(ENTITY_TYPE_ROOT_ELEMENT) &&
				(
						getParentParser().latestOpenedElement(1).equalsIgnoreCase(ENTITY_TYPES_ROOT_ELEMENT)
							|| getParentParser().latestOpenedElement(1).equalsIgnoreCase(ENTITY_TYPE_CHILD_TYPES_ELEMENT)
				)) {

			EntityType parentType = VirtualRootType.getInstance();

			if (!parserStack.isEmpty()) {
				parentType = parserStack.getFirst().getArtifact();
			}

			parserStack.push(new EntityTypeParser(parentParser, entityTypes, parentType));
			return;
		}

		if (parserStack.isEmpty()) {
			return;
		}

		parserStack.getFirst().startElement(element);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void elementValue(String element, String value) {
		if (parserStack.isEmpty()) {
			return;
		}

		parserStack.getFirst().elementValue(element, value);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void endElement(String element) {
		// close latest (child) entityType?
		if (element.equalsIgnoreCase(ENTITY_TYPE_ROOT_ELEMENT) &&
				(
						parentParser.latestOpenedElement(0).equalsIgnoreCase(ENTITY_TYPE_CHILD_TYPES_ELEMENT)
								|| parentParser.latestOpenedElement(0).equalsIgnoreCase(ENTITY_TYPES_ROOT_ELEMENT)
				)) {

			if (!parserStack.isEmpty()) {
				EntityTypeParser parser = parserStack.pop();
				parser.finish();
			}
			return;
		}

		if (parserStack.isEmpty()) {
			return;
		}

		parserStack.getFirst().endElement(element);
	}
}
