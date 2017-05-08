package de.hpi.bpt.argos.storage.dataModel.attribute.type;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "TypeAttribute")
public class TypeAttributeImpl extends PersistenceArtifactImpl implements TypeAttribute {
    @Column(name = "TypeId")
    private long typeId;

    @Column(name = "Name")
    private String name;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTypeId() {
        return typeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }
}
