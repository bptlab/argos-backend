package de.hpi.bpt.argos.parsing.staticData.subParser;

import de.hpi.bpt.argos.parsing.XMLSubParser;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;

/**
 * This interface represents XMLSubParser which are responsible for parsing a single entityType.
 */
public interface EntityTypeParser extends XMLSubParser {

	/**
	 * This method returns the parentType of the entityType, which is parsed by this parser.
	 * @return - the parentType of the currently parsed entityType
	 */
	EntityType getParentEntityType();

	/**
	 * This method returns the currently parsed entityType.
	 * @return - the currently parsed entityType
	 */
	EntityType getEntityType();

	/**
	 * This method finishes the parsing process for this entityType.
	 */
	void finish();
}
