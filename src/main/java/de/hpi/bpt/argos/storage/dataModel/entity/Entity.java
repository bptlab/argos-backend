package de.hpi.bpt.argos.storage.dataModel.entity;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents an entity. (e.g. Configurations of products)
 */
public interface Entity extends PersistenceArtifact {
    /**
     * This method returns the name of this entity.
     * @return - the name of this entity
     */
    String getName();

    /**
     * This method sets the name of this entity.
     * @param name - the name of this entity to be set.
     */
    void setName(String name);

    /**
     * This method returns the parent entity id of this entity.
     * @return - the parent entity id of this entity
     */
    long getParentId();

    /**
     * This method sets the id of the parent entity of this entity.
     * @param parentId - the id of the parent entity of this entity to be set
     */
    void setParentId(long parentId);

    /**
     * This method returns the entityType id of this entity.
     * @return - the entityType id of this entity
     */
    long getTypeId();

    /**
     * This method sets the id of the entityType of this entity.
     * @param typeId - the id of the entityType of this entity to be set
     */
    void setTypeId(long typeId);

    /**
     * This method returns the status of this entity.
     * @return - the status of this entity
     */
    String getStatus();

    /**
     * This method sets the status of this entity.
     * @param status - the status of this entity to be set
     */
    void setStatus(String status);

}
