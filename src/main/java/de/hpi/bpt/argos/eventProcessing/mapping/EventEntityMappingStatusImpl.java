package de.hpi.bpt.argos.eventProcessing.mapping;

import de.hpi.bpt.argos.storage.dataModel.entity.Entity;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventEntityMappingStatusImpl implements EventEntityMappingStatus {

	private boolean mapped;
	private Entity eventOwner;

	/**
	 * This constructor initializes all members with their default values.
	 */
	public EventEntityMappingStatusImpl() {
		mapped = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isMapped() {
		return mapped;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Entity getEventOwner() {
		return eventOwner;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventOwner(Entity eventOwner) {
		this.eventOwner = eventOwner;
		mapped = true;
	}
}
