package de.hpi.bpt.argos.persistence.model.product.error;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "ErrorCause")
public class ErrorCauseImpl extends PersistenceEntityImpl implements ErrorCause {

	@Column(name = "Description")
	protected String description = "";

	@Column(name = "Occurrences")
	protected long errorOccurrences = 0;

	@Column(name = "Prediction")
	protected double errorPrediction = 0;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getErrorOccurrences() {
		return errorOccurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setErrorOccurrences(long errorOccurrences) {
		this.errorOccurrences = errorOccurrences;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incrementErrorOccurrences(long count) {
		errorOccurrences += count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getErrorPrediction() {
		return errorPrediction;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setErrorPrediction(double errorPrediction) {
		this.errorPrediction = errorPrediction;
	}
}
