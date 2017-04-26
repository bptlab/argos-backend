package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents observers, which are interested in persistenceArtifact updates.
 */
@FunctionalInterface
public interface PersistenceArtifactUpdateObserver {

	/**
	 * This method gets called, whenever a persistenceArtifact is updated.
	 * @param updateType - the type of the update
	 * @param updatedArtifact - the updated artifact
	 * @param fetchUri - the uri, from where the updated artifact can be fetched
	 */
	void onArtifactUpdated(PersistenceArtifactUpdateType updateType, PersistenceArtifact updatedArtifact, String fetchUri);
}
