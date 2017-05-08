package de.hpi.bpt.argos.eventProcessing.status;

import de.hpi.bpt.argos.api.entity.EntityEndpoint;
import de.hpi.bpt.argos.common.Observable;
import de.hpi.bpt.argos.eventProcessing.mapping.EventMappingObserver;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.event.Event;
import de.hpi.bpt.argos.storage.dataModel.mapping.EventEntityMapping;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EntityStatusCalculatorImpl implements EntityStatusCalculator {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(Observable<EventMappingObserver> eventEntityMapper) {
		eventEntityMapper.subscribe(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEventMapped(Event event, Entity entity, EventEntityMapping usedMapping) {
		// this is the default behavior, feel free to implement your own status-logic here
		changeStatusBasedOnMapping(entity, usedMapping);
	}

	/**
	 * This method sets the new entity status according to the used mapping.
	 * @param entity - the affected entity
	 * @param mapping - the applied eventEntityMapping
	 */
	private void changeStatusBasedOnMapping(Entity entity, EventEntityMapping mapping) {
		if (mapping.getTargetStatus() == null || mapping.getTargetStatus().length() == 0) {
			return;
		}

		entity.setStatus(mapping.getTargetStatus());
		PersistenceAdapterImpl.getInstance().updateArtifact(entity, EntityEndpoint.getEntityUri(entity.getId()));
	}
}
