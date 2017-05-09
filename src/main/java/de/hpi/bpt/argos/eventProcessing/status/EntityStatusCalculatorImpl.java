package de.hpi.bpt.argos.eventProcessing.status;

import de.hpi.bpt.argos.api.entity.EntityEndpoint;
import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatus;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
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
	public void setup(EventReceiver eventReceiver) {
		eventReceiver.getEventMappingObservable().subscribe(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void onEventMapped(EventEntityMappingStatus processStatus) {

		if (processStatus.getStatusUpdateStatus().isStatusUpdated()) {
			return;
		}

		changeStatusBasedOnMapping(processStatus.getEventOwner(), processStatus.getUsedMapping());
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
