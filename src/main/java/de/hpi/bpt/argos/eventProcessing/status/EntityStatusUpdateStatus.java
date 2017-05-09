package de.hpi.bpt.argos.eventProcessing.status;

/**
 * This interface holding the state of the entityStatus calculation after an entityMapping was successful.
 */
public interface EntityStatusUpdateStatus {

	/**
	 * This method returns the status of the entity, before any status updated were performed.
	 * @return -the status of the entity, before any status updated were performed
	 */
	String getOldStatus();

	/**
	 * This method returns the new status of the entity, after the status was changed.
	 * @return - the new status of the entity, or null
	 */
	String getNewStatus();

	/**
	 * This method indicates whether the entity status was updated or not.
	 * @return - true, if the status of the entity changed already
	 */
	boolean isStatusUpdated();

	/**
	 * This method sets the new status of the entity.
	 * @param newStatus - the status to be set
	 */
	void setNewStatus(String newStatus);
}
