package de.hpi.bpt.argos.storage.dataModel.mapping;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventEntityMapping")
public class EventEntityMappingImpl extends PersistenceArtifactImpl implements EventEntityMapping {

    @Column(name = "EventTypeId")
    private long eventTypeId;

    @Column(name = "EntityTypeId")
    private long entityTypeId;

    @Column(name = "TargetStatus")
    private String targetStatus;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEventTypeId() {
        return eventTypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEventTypeId(long eventTypeId) {
        this.eventTypeId = eventTypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEntityTypeId() {
        return entityTypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEntityTypeId(long entityTypeId) {
        this.entityTypeId = entityTypeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTargetStatus() {
        return targetStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTargetStatus(String targetStatus) {
        this.targetStatus = targetStatus;
    }
}
