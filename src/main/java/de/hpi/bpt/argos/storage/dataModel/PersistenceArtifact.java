package de.hpi.bpt.argos.storage.dataModel;

/**
 * This interface represents all kinds of storeable artifact.
 */
public interface PersistenceArtifact {

	/**
	 * This method returns the unique identifier for this artifact.
	 * @return - the unique identifier for this artifact.
	 */
	long getId();

	/**
	 * This method returns the timestamp, when this artifact was created.
	 * @return - the timestamp, when this artifact was created
	 */
	long getCreationTimestamp();
}
