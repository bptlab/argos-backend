package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "ErrorType")
public class ErrorTypeImpl extends PersistenceEntityImpl implements ErrorType {

	@Column(name = "CauseCode")
	protected int causeCode = 0;

	@Column(name = "ErrorOccurrences")
	protected long errorOccurrences = 0;

	@Column(name = "ErrorPrediction")
	protected double errorPrediction = 0;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getCauseCode() {
		return causeCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setCauseCode(int causeCode) {
		this.causeCode = causeCode;
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
		this.errorOccurrences += count;
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
