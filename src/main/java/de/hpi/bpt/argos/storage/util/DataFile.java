package de.hpi.bpt.argos.storage.util;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents file on the local file system, which are used for any kind of data parsing.
 */
public interface DataFile extends PersistenceArtifact {

	/**
	 * This method returns the full path to this dataFile.
	 * @return - the full path to this dataFile
	 */
	String getPath();

	/**
	 * This method sets the full path to this dataFile.
	 * @param path - the path to be set
	 */
	void setPath(String path);

	/**
	 * This method returns the timestamp of the latest modification of this dataFile.
	 * @return - the timestamp of the latest modification of this dataFile
	 */
	long getModificationTimestamp();

	/**
	 * This method sets the timestamp of the last modification of this dataFile.
	 * @param modificationTimestamp - the timestamp to be set
	 */
	void setModificationTimestamp(long modificationTimestamp);
}
