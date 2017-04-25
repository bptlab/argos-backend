package de.hpi.bpt.argos.storage.dataModel.mapping;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents the mapping condition between an eventTypeAttribute and an entityTypeAttribute.
 * This mapping belongs to an eventEntityMapping.
 */
public interface MappingCondition extends PersistenceArtifact {
    /**
     * This method returns the mapping id of this mapping condition.
     * @return eventEntityMapping id of this mapping condition
     */
    long getMappingId();

    /**
     * This method sets the mapping id of this mapping condition.
     * @param mappingId the id of the eventEntityMapping to be set
     */
    void setMappingId(long mappingId);

    /**
     * This method returns the eventTypeAttribute id of this mapping condition.
     * @return eventTypeAttribute id of this mapping condition
     */
    long getEventTypeAttributeId();

    /**
     * This method sets the eventTypeAttribute id of this mapping condition.
     * @param eventTypeAttributeId the id of the eventTypeAttribute to be set
     */
    void setEventTypeAttributeId(long eventTypeAttributeId);

    /**
     * This method returns the entityTypeAttribute of this mapping condition.
     * @return entityTypeAttribute id of this mapping
     */
    long getEntityTypeAttributeId();

    /**
     * This method sets the entityTypeAttribute id of this mapping condition.
     * @param entityTypeAttributeId the id of the entityTypeAttribute to be set
     */
    void setEntityTypeAttributeId(long entityTypeAttributeId);
}
