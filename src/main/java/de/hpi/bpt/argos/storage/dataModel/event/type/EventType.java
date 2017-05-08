package de.hpi.bpt.argos.storage.dataModel.event.type;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents the type of an event.
 */
public interface EventType extends PersistenceArtifact {
    /**
     * This method returns the name of this eventType.
     * @return - name of this eventType
     */
    String getName();

    /**
     * This method sets the  name of this eventType.
     * @param name - name of this eventType to be set
     */
    void setName(String name);

    /**
     * This method returns whether this eventType is deletable.
     * @return - true, if eventType is deletable
     */
    boolean isDeletable();

    /**
     * This method sets if this eventType is deletable.
     * @param deletable - determine if this eventType is deletable
     */
    void setDeletable(boolean deletable);

    /**
     * This method returns the id of the timestamp attribute of this eventType.
     * @return - timestampAttribute id of this eventType
     */
    long getTimeStampAttributeId();

    /**
     * This method sets the timestampAttribute id of this eventType.
     * @param timeStampAttributeId - id of the timestampAttribute of this event
     */
    void setTimeStampAttributeId(long timeStampAttributeId);

	/**
	 * This method indicates whether this eventType should be registered in the eventProcessingPlatform.
	 * @return - true, if this eventType should be registered in the eventProcessingPlatform
	 */
	boolean shouldBeRegistered();

	/**
	 * This method sets if this eventType should be registered in the eventProcessingPlatform.
	 * @param shouldBeRegistered - indicator whether this eventType should be registered in the eventPlatform to be set
	 */
    void setShouldBeRegistered(boolean shouldBeRegistered);
}
