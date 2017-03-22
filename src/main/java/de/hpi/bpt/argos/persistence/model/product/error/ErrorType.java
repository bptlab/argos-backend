package de.hpi.bpt.argos.persistence.model.product.error;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

import java.util.Set;

/**
 * This interface represents error types which occur for products.
 */
public interface ErrorType extends PersistenceEntity {

	/**
	 * This method returns the cause code for this error type.
	 * @return - the cause code for this event type.
	 */
	int getCauseCode();

	/**
	 * This method sets the cause code for this error type.
	 * @param causeCode - the cause code to be set
	 */
	void setCauseCode(int causeCode);

	/**
	 * This method returns a set of error causes, which might lead to an event of this error type.
	 * @return - a set of error causes
	 */
	Set<ErrorCause> getErrorCauses();

	/**
	 * This method returns the error cause with the corresponding description, or null.
	 * @param description - the description to search for
	 * @return - the error cause, or null
	 */
	ErrorCause getErrorCause(String description);

	/**
	 * This method sets the error causes, which might lead to an event of this error type.
	 * @param errorCauses - the error causes to be set
	 */
	void setErrorCauses(Set<ErrorCause> errorCauses);

	/**
	 * This method adds a new error cause to this error type.
	 * @param errorCause - the error cause to be added
	 */
	void addErrorCause(ErrorCause errorCause);
}
