package de.hpi.bpt.argos.storage.dataModel.event.type;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EventType")
public class EventTypeImpl extends PersistenceArtifactImpl implements EventType {

    @Column(name = "Name")
    private String name;

    @Column (name = "Deletable")
    private boolean deletable;

    @Column (name = "TimestampAttributeId")
    private long timestampAttributeId;

    @Column(name = "ShouldBeRegistered")
    private boolean shouldBeRegistered;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDeletable() {
        return deletable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long getTimeStampAttributeId() {
        return timestampAttributeId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setTimeStampAttributeId(long timeStampAttributeId) {
        this.timestampAttributeId = timeStampAttributeId;
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean shouldBeRegistered() {
		return shouldBeRegistered;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setShouldBeRegistered(boolean shouldBeRegistered) {
		this.shouldBeRegistered = shouldBeRegistered;
	}
}
