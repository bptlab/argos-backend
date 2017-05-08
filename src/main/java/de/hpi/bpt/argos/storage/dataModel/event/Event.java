package de.hpi.bpt.argos.storage.dataModel.event;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents an event.
 */
public interface Event extends PersistenceArtifact {

    /**
     * This method returns the id of the eventType of this event.
     * @return eventType id of this event
     */
    long getTypeId();

    /**
     * This method sets the id of the eventType of this event.
     * @param typeId eventType id of this event to be set
     */
    void setTypeId(long typeId);

    /**
     * This method returns the id of the corresponding entity of this event.
     * @return entity id of this event
     */
    long getEntityId();

    /**
     * This method sets the id of the entity of this event.
     * @param entityId id of the corresponding entity of this event
     */
    void setEntityId(long entityId);
}
