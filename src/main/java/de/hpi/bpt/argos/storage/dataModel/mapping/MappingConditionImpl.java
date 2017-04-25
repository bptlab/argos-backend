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
@Table(name = "MappingCondition")
public class MappingConditionImpl extends PersistenceArtifactImpl implements MappingCondition {
    @Column(name = "MappingId")
    private long mappingId;

    @Column(name = "EntityTypeAttributeId")
    private long entityTypeAttributeId;

    @Column(name = "EventTypeAttributeId")
    private long eventTypeAttributeId;

    /**
     * {@inheritDoc}
     */
    @Override
    public long getMappingId() {
        return mappingId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setMappingId(long mappingId) {
        this.mappingId = mappingId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEventTypeAttributeId() {
        return eventTypeAttributeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEventTypeAttributeId(long eventTypeAttributeId) {
        this.eventTypeAttributeId = eventTypeAttributeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getEntityTypeAttributeId() {
        return entityTypeAttributeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEntityTypeAttributeId(long entityTypeAttributeId) {
        this.entityTypeAttributeId = entityTypeAttributeId;
    }
}
