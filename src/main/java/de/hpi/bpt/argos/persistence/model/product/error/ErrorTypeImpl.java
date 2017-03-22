package de.hpi.bpt.argos.persistence.model.product.error;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "ErrorType")
public class ErrorTypeImpl extends PersistenceEntityImpl implements ErrorType {

	@Column(name = "CauseCode")
	protected int causeCode = 0;

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = ErrorCauseImpl.class)
	protected Set<ErrorCause> errorCauses = new HashSet<>();

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
	public Set<ErrorCause> getErrorCauses() {
		return errorCauses;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ErrorCause getErrorCause(String description) {
		for (ErrorCause cause : errorCauses) {
			if (cause.getDescription().equalsIgnoreCase(description)) {
				return cause;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setErrorCauses(Set<ErrorCause> errorCauses) {
		this.errorCauses = errorCauses;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addErrorCause(ErrorCause errorCause) {
		errorCauses.add(errorCause);
	}
}
