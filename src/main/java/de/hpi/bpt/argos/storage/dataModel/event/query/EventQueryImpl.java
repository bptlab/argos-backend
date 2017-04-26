package de.hpi.bpt.argos.storage.dataModel.event.query;

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
public class EventQueryImpl extends PersistenceArtifactImpl implements EventQuery {
    @Column(name = "Uuid")
    private String uuid;

    @Column(name = "Query")
    private String query;

    @Column(name = "Description")
    private String description;

    @Column(name = "TypeId")
    private long typeId;

    @Column(name = "TargetStatus")
    private String targetStatus;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUuid() {
        return uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getQuery() {
        return query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setQuery(String query) {
        this.query = query;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDescription() {
        return description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDescription(String description) {
        this.description = description;
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