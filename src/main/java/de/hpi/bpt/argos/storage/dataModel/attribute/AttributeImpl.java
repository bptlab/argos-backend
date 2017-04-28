package de.hpi.bpt.argos.storage.dataModel.attribute;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "Attribute")
public class AttributeImpl extends PersistenceArtifactImpl implements Attribute {

    @Column(name = "Value")
    private String value;

    @Column(name = "TypeAttributeId")
    private long typeAttributeId;

    @Column(name = "OwnerId")
    private long ownerId;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getValue() {
        return value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTypeAttributeId() {
        return typeAttributeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeAttributeId(long typeAttributeId) {
        this.typeAttributeId = typeAttributeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getOwnerId() {
        return ownerId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOwnerId(long ownerId) {
        this.ownerId = ownerId;
    }
}
