package de.hpi.bpt.argos.parsing.util;

import java.io.File;

/**
 * This interface offers util methods for file access.
 */
public interface FileUtil {

	/**
	 * This method indicates whether a file was modified since the last check.
	 * @param file - the file to check
	 * @return - true, if the file was modified since last check
	 */
	boolean wasModified(File file);

	/**
	 * This method sets the modification timestamp of a given file in the database.
	 * @param file - the file to update
	 */
	void modify(File file);
}
