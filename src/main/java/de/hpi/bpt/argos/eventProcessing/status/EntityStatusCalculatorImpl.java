package de.hpi.bpt.argos.eventProcessing.status;

import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatus;
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

		if (processStatus.getUsedMapping() == null || processStatus.getStatusUpdateStatus().isStatusUpdated()) {
			return;
		}

		changeStatusBasedOnMapping(processStatus);
	}

	/**
	 * This method sets the new entity status according to the used mapping.
	 * @param processStatus - the current status of the mapping process
	 */
	private void changeStatusBasedOnMapping(EventEntityMappingStatus processStatus) {
		EventEntityMapping mapping = processStatus.getUsedMapping();
		if (mapping.getTargetStatus() == null || mapping.getTargetStatus().length() == 0) {
			return;
		}

		processStatus.getStatusUpdateStatus().setNewStatus(mapping.getTargetStatus());
	}
}
