package de.hpi.bpt.argos.storage.dataModel.entity;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@javax.persistence.Entity
@Table(name = "Entity")
public class EntityImpl extends PersistenceArtifactImpl implements Entity {

    @Column(name = "ParentId")
    protected long parentId;

    @Column(name = "Name")
    protected String name;

    @Column(name = "TypeId")
    protected long typeId;

    @Column(name = "Status")
    protected String status;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public long getParentId() {
        return parentId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

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
    public String getStatus() {
        return status;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setStatus(String status) {
        this.status = status;
    }
}
