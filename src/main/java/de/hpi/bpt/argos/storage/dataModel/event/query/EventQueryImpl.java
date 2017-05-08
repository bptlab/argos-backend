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
@Table(name = "EventQuery")
public class EventQueryImpl extends PersistenceArtifactImpl implements EventQuery {
    @Column(name = "Uuid")
    private String uuid;

    @Column(name = "Query", columnDefinition = "LONGTEXT")
    private String query;

    @Column(name = "Description", columnDefinition = "LONGTEXT")
    private String description;

    @Column(name = "TypeId")
    private long typeId;

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
}
