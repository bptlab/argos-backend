package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

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
	 * This method returns the number of error events which occurred for this error type.
	 * @return - the number of error events which occurred for this error type
	 */
	long getErrorOccurrences();

	/**
	 * This method sets the number of error events which occurred for this error type.
	 * @param errorOccurrences - the number of error occurrences to be set
	 */
	void setErrorOccurrences(long errorOccurrences);

	/**
	 * This method increments the number of error events which occurred for this error type.
	 * @param count - the number to increment
	 */
	void incrementErrorOccurrences(long count);

	/**
	 * This method returns the predicted relative error occurrences.
	 * @return - the predicted relative error occurrences
	 */
	double getErrorPrediction();

	/**
	 * This method sets the predicted relative error occurrences.
	 * @param errorPrediction - the errorPrediction to be set
	 */
	void setErrorPrediction(double errorPrediction);
}
