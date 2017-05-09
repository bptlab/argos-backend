package de.hpi.bpt.argos.eventProcessing.mapping;

import de.hpi.bpt.argos.eventProcessing.status.EntityStatusUpdateStatus;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;

/**
 * This interface represents a status, which is passed to any observers of the mapping process.
 */
public interface EventEntityMappingStatus {

	/**
	 * This method returns the current mapping status of the process.
	 * @return - true, if the event was mapped to an entity already
	 */
	boolean isMapped();

	/**
	 * This method returns the owner of the event, if the event was already mapped.
	 * @return - the owner of the event, if the event was already mapped
	 */
	Entity getEventOwner();

	/**
	 * This method returns the event, which is about to be mapped.
	 * @return - returns the event, which is about to be mapped
	 */
	Event getEvent();

	/**
	 * This method returns the applied eventEntityMapping.
	 * @return - the applied eventEntityMapping
	 */
	EventEntityMapping getUsedMapping();

	/**
	 * This method sets the owner of the event.
	 * @param eventOwner - the owner of the event to be set
	 * @param usedMapping - the applied eventEntityMapping
	 */
	void setEventOwner(Entity eventOwner, EventEntityMapping usedMapping);

	/**
	 * This method returns the entityStatusUpdateStatus, which holds the state of the entity status update process.
	 * @return - the entityStatusUpdateStatus, which holds the state of the entity status update process
	 */
	EntityStatusUpdateStatus getStatusUpdateStatus();
}
