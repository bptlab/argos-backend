package de.hpi.bpt.argos.parsing.util;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents a batch, which will store artifacts in the database and reduces database calls.
 */
public interface ArtifactBatch {

	/**
	 * This method adds a list of artifacts to the batch.
	 * @param artifacts - a list of artifacts to store in the database
	 */
	void add(PersistenceArtifact... artifacts);

	/**
	 * This method saves all remaining artifacts in the database.
	 */
	void finish();

	/**
	 * This method returns the total amount of artifacts stored in the database.
	 * @return - the total amount of artifacts stored in the database
	 */
	long getTotalArtifactsStored();
}
