package de.hpi.bpt.argos.storage.dataModel.entity;

/**
 * This interface represents all kinds of storeable artifact.
 */
public interface PersistenceArtifact {

	/**
	 * This method returns the unique identifier for this artifact.
	 * @return - the unique identifier for this artifact.
	 */
	long getId();
}
