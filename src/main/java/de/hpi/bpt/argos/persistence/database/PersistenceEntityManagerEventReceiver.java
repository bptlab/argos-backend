package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.notifications.PushNotificationType;

/**
 * This interface represents objects which should receive events, whenever an entity is created or modified.
 */
public interface PersistenceEntityManagerEventReceiver {

	/**
	 * This method gets called whenever an entity is updated (e.g. created or modified).
	 * @param typeOfUpdate - the type of modification (create or update)
	 * @param entity - the modified entity
	 * @param fetchUri - the uri to fetch this entity
	 */
	void onEntityModified(PushNotificationType typeOfUpdate, PersistenceEntity entity, String fetchUri);
}
