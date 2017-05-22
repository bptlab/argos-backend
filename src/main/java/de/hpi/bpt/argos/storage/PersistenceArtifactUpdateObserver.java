package de.hpi.bpt.argos.storage;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;

/**
 * This interface represents observers, which are interested in persistenceArtifact updates.
 */
public interface PersistenceArtifactUpdateObserver {

	/**
	 * This method gets called, whenever a persistenceArtifact is updated.
	 * @param updateType - the type of the update
	 * @param updatedArtifact - the updated artifact
	 * @param fetchUri - the uri, from where the updated artifact can be fetched
	 */
	void onArtifactUpdated(PersistenceArtifactUpdateType updateType, PersistenceArtifact updatedArtifact, String fetchUri);

	/**
	 * This method gets called, whenever a new event was created.
	 * @param eventOwner - the owner of the event
	 * @param event - the event
	 * @param fetchUri - the uri, from where the new event can be fetched
	 */
	void onEventCreation(Entity eventOwner, Event event, String fetchUri);
}
