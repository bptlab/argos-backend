package de.hpi.bpt.argos.parsing;

/**
 * This interface offers methods to parse eventTypes from .json files.
 */
public interface EventTypeParser {

	/**
	 * This method loads all eventTypes from disk.
	 */
	void loadEventTypes();
}
