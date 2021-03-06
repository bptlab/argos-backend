package de.hpi.bpt.argos.storage.dataModel.mapping;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents a mapping between an eventType and an entityType.
 */
public interface EventEntityMapping extends PersistenceArtifact {
    /**
     * This method returns the eventType id of this mapping.
     * @return eventType id of this mapping
     */
    long getEventTypeId();

    /**
     * This method sets the eventType id of this mapping.
     * @param eventTypeId the id of the eventType to be set
     */
    void setEventTypeId(long eventTypeId);

    /**
     * This method returns the entityType of this mapping.
     * @return entityType id of this mapping
     */
    long getEntityTypeId();

    /**
     * This method sets the entityType id of this mapping.
     * @param entityTypeId the id of the entityType to be set
     */
    void setEntityTypeId(long entityTypeId);

    /**
     * This method returns the new status of the entity, after this mapping has been applied.
     * @return - the new status of the entity, after this mapping has been applied
     */
    String getTargetStatus();

    /**
     * This method sets the new status of the entity, after this mapping has been applied.
     * @param targetStatus - the new status of the entity to be set
     */
    void setTargetStatus(String targetStatus);
}
