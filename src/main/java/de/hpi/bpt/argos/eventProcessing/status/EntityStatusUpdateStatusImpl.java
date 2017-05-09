package de.hpi.bpt.argos.eventProcessing.status;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EntityStatusUpdateStatusImpl implements EntityStatusUpdateStatus {

	private String oldStatus;
	private String newStatus;
	private boolean statusChanged;

	/**
	 * This constructor initializes the oldStatus member with the given parameter.
	 * @param currentEntityStatus - the current entity status, before any changed were performed
	 */
	public EntityStatusUpdateStatusImpl(String currentEntityStatus) {
		oldStatus = currentEntityStatus;
		statusChanged = false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getOldStatus() {
		return oldStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getNewStatus() {
		return newStatus;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStatusUpdated() {
		return statusChanged;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNewStatus(String newStatus) {
		statusChanged = true;
		this.newStatus = newStatus;
	}
}
