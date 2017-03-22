package de.hpi.bpt.argos.persistence.model.product.error;

import de.hpi.bpt.argos.persistence.database.PersistenceEntity;

/**
 * This interface represents causes for different error types.
 */
public interface ErrorCause extends PersistenceEntity {

	/**
	 * This method gets the error cause description.
	 * @return - the error cause description
	 */
	String getDescription();

	/**
	 * This method sets the error cause description.
	 * @param description - the description to be set
	 */
	void setDescription(String description);

	/**
	 * This method returns the number of error events which occurred for this error cause.
	 * @return - the number of error events which occurred for this error cause
	 */
	long getErrorOccurrences();

	/**
	 * This method sets the number of error events which occurred for this error cause.
	 * @param errorOccurrences - the number of error occurrences to be set
	 */
	void setErrorOccurrences(long errorOccurrences);

	/**
	 * This method increments the number of error events which occurred for this error cause.
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
