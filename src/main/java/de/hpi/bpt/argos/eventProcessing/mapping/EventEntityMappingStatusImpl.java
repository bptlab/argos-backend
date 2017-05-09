package de.hpi.bpt.argos.eventProcessing.mapping;

import de.hpi.bpt.argos.eventProcessing.status.EntityStatusUpdateStatus;
import de.hpi.bpt.argos.eventProcessing.status.EntityStatusUpdateStatusImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventEntityMappingStatusImpl implements EventEntityMappingStatus {

	private boolean mapped;
	private Entity eventOwner;
	private Event event;
	private EventEntityMapping mapping;
	private EntityStatusUpdateStatus statusUpdateStatus;

	/**
	 * This constructor initializes all members with their default values.
	 * @param event - the event, which is about to be mapped
	 */
	public EventEntityMappingStatusImpl(Event event) {
		this.event = event;
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
	public Event getEvent() {
		return event;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventEntityMapping getUsedMapping() {
		return mapping;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventOwner(Entity eventOwner, EventEntityMapping usedMapping) {
		this.eventOwner = eventOwner;
		mapping = usedMapping;
		mapped = true;

		statusUpdateStatus = new EntityStatusUpdateStatusImpl(eventOwner.getStatus());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityStatusUpdateStatus getStatusUpdateStatus() {
		return statusUpdateStatus;
	}
}
