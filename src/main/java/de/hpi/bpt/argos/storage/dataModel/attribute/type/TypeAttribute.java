package de.hpi.bpt.argos.storage.dataModel.attribute.type;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents the attributes of entity- or event types.
 */
public interface TypeAttribute extends PersistenceArtifact {
    /**
     * This method returns the id of the type this typeAttribute belongs to.
     * @return - the type id of the type of this entityType
     */
    long getTypeId();

    /**
     * This method sets the id of the type this typeAttribute belongs to.
     * @param typeId - the id of the type of this entityType to be set
     */
    void setTypeId(long typeId);

    /**
     * This method returns the name of this entityType.
     * @return - the name of this entityType
     */
    String getName();

    /**
     * This method sets the name of this entityType.
     * @param name - the name of this entityType to be set.
     */
    void setName(String name);
}
