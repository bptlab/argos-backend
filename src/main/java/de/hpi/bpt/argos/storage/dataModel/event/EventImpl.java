package de.hpi.bpt.argos.storage.dataModel.event;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "Event")
public class EventImpl extends PersistenceArtifactImpl implements Event {

    @Column(name = "TypeId")
    private long typeId;

    @Column(name = "EntityId")
    private long entityId;

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
    public long getEntityId() {
        return entityId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEntityId(long entityId) {
        this.entityId = entityId;
    }
}
