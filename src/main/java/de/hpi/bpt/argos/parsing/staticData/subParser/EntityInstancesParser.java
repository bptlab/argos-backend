package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLFileParser;
import de.hpi.bpt.argos.parsing.XMLSubParserImpl;
import de.hpi.bpt.argos.parsing.staticData.EntityTypeList;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.VirtualRoot;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * {@inheritDoc}
 * This subParser is responsible for parsing entityInstances.
 */
public class EntityInstancesParser extends XMLSubParserImpl {

	private static final String ENTITY_INSTANCES_ELEMENT = "instances";
	private static final String ENTITY_INSTANCE_ELEMENT = "instance";
	private static final String ENTITY_INSTANCE_CHILD_INSTANCES_ELEMENT = "childInstances";

	private Deque<EntityInstanceParser> parserStack;
	private EntityTypeList entityTypes;

	/**
	 * This constructor sets the parent parser.
	 * @param parent - the parent parser to be set
	 * @param entityTypes - all entityTypes which are valid for the coming entityInstances.
	 */
	public EntityInstancesParser(XMLFileParser parent, EntityTypeList entityTypes) {
		super(parent);
		parserStack = new ArrayDeque<>();
		this.entityTypes = entityTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void startElement(String element) {
		// new (child) instance?
		if (element.equalsIgnoreCase(ENTITY_INSTANCE_ELEMENT)
				&& (
						getParentParser().latestOpenedElement(1).equalsIgnoreCase(ENTITY_INSTANCES_ELEMENT)
								|| getParentParser().latestOpenedElement(1).equalsIgnoreCase(ENTITY_INSTANCE_CHILD_INSTANCES_ELEMENT)
				)) {

			Entity parentEntity = VirtualRoot.getInstance();

			if (!parserStack.isEmpty()) {
				parentEntity = parserStack.getFirst().getArtifact();
			}

			parserStack.push(new EntityInstanceParser(parentParser, entityTypes, parentEntity));
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
		// close latest (child) instance?
		if (element.equalsIgnoreCase(ENTITY_INSTANCE_ELEMENT)
				&& (
						parentParser.latestOpenedElement(0).equalsIgnoreCase(ENTITY_INSTANCE_CHILD_INSTANCES_ELEMENT)
								|| parentParser.latestOpenedElement(0).equalsIgnoreCase(ENTITY_INSTANCES_ELEMENT)
				)) {

			if (!parserStack.isEmpty()) {
				EntityInstanceParser parser = parserStack.pop();
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
