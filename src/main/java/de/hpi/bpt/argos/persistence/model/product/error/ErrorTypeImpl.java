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

	@Column(name = "ErrorTypeId")
	protected String errorTypeId = "";

	@Column(name = "DisplayCode")
	protected String displayCode = "";

	@Column(name = "CauseCode")
	protected int causeCode = 0;

	@Column(name = "Description")
	protected String description = "";

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = ErrorCauseImpl.class)
	protected Set<ErrorCause> errorCauses = new HashSet<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorTypeId() {
		return errorTypeId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setErrorTypeId(String displayCode, int causeCode) {
		errorTypeId = String.format("%1$s-%2$s", displayCode, causeCode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDisplayCode() {
		return displayCode;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setDisplayCode(String displayCode) {
		this.displayCode = displayCode;
		setErrorTypeId(displayCode, causeCode);
	}

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
		setErrorTypeId(displayCode, causeCode);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getErrorDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setErrorDescription(String errorDescription) {
		description = errorDescription;
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
