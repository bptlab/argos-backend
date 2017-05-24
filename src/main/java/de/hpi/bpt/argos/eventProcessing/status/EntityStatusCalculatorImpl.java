package de.hpi.bpt.argos.eventProcessing.status;

import de.hpi.bpt.argos.eventProcessing.EventReceiver;
import de.hpi.bpt.argos.eventProcessing.mapping.EventEntityMappingStatus;

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
		String targetStatus = processStatus.getUsedMapping().getTargetStatus();
		if (targetStatus == null || targetStatus.length() == 0) {
			return;
		}

		processStatus.getStatusUpdateStatus().setNewStatus(targetStatus);
	}
}
