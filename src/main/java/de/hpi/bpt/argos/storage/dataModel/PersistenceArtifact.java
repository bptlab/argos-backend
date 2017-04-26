package de.hpi.bpt.argos.storage.dataModel;

/**
 * This interface represents all kinds of storeable artifact.
 */
@FunctionalInterface
public interface PersistenceArtifact {

	/**
	 * This method returns the unique identifier for this artifact.
	 * @return - the unique identifier for this artifact.
	 */
	long getId();
}