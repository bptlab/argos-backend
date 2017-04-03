package de.hpi.bpt.argos.persistence.model.parsing;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

/**
 * This interface represents a data file, which was loaded into the backend.
 */
public interface DataFile extends PersistenceEntity {

	/**
	 * This method returns the path of the data file.
	 * @return - the path of the data file
	 */
	String getPath();

	/**
	 * This method sets the path of this data file.
	 * @param path - the path to be set
	 */
	void setPath(String path);

	/**
	 * This method returns the last modification timestamp of this data file.
	 * @return - the last modification timestamp of this data file
	 */
	long getModificationTimestamp();

	/**
	 * This method sets the last modification timestamp of this data file.
	 * @param modificationTimestamp - the modification timestamp to be set
	 */
	void setModificationTimestamp(long modificationTimestamp);

}
