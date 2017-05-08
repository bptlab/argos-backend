package de.hpi.bpt.argos.storage.dataModel.attribute;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents an attribute of an event or entity.
 */
public interface Attribute extends PersistenceArtifact {
    /**
     * This method returns the value of this attribute.
     * @return - the value of this attribute
     */
    String getValue();

    /**
     * This method sets the value of this attribute.
     * @param value - the value of this attribute to be set
     */
    void setValue(String value);

    /**
     * This method returns the typeAttribute id of this attribute.
     * @return - the typeAttribute id of this attribute
     */
    long getTypeAttributeId();

    /**
     * This method sets the typeAttribute id of this attribute.
     * @param typeAttributeId - the typeAttribute id of this attribute to be set
     */
    void setTypeAttributeId(long typeAttributeId);

    /**
     * This method returns the id of the corresponding entity/event of this attribute.
     * @return - the owner id of this attribute
     */
    long getOwnerId();

    /**
     * This method sets the id of the corresponding entity/event of this attribute.
     * @param ownerId - the owner id of this attribute to be set
     */
    void setOwnerId(long ownerId);
}
