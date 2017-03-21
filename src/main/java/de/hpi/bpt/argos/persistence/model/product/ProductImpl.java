package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.event.EventQuery;
import de.hpi.bpt.argos.persistence.model.event.EventQueryImpl;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "Product")
public class ProductImpl extends PersistenceEntityImpl implements Product {

	@ManyToOne(cascade = {CascadeType.ALL}, targetEntity = ProductFamilyImpl.class)
	@JoinColumn(name = "product_family_Id")
	protected ProductFamily productFamily;

	@Column(name = "ProductionStart")
	protected Date productionStart = new Date();

	@Column(name = "State")
	protected ProductState state = ProductState.UNDEFINED;

	@Column(name = "Name")
	protected String name = "Unknown Product";

	@Column(name = "OrderNumber")
	protected int orderNumber = 0;

	@Column(name = "StateDescription")
	protected String stateDescription = "State is not defined yet";

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventQueryImpl.class)
	protected EventQuery transitionToRunningState = new EventQueryImpl();

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventQueryImpl.class)
	protected EventQuery transitionToWarningState = new EventQueryImpl();

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = EventQueryImpl.class)
	protected EventQuery transitionToErrorState = new EventQueryImpl();

	@Column(name = "NumberOfDevices")
	protected long numberOfDevices = 0;

	@Column(name = "NumberOfEvents")
	protected long numberOfEvents = 0;

	@OneToMany(cascade = {CascadeType.ALL}, fetch = FetchType.EAGER, targetEntity = ErrorTypeImpl.class)
	protected Set<ErrorType> errorTypes = new HashSet<>();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductFamily getProductFamily() {
		return productFamily;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductFamily(ProductFamily productFamily) {
		this.productFamily = productFamily;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Date getProductionStart() {
		return productionStart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setProductionStart(Date productionStart) {
		this.productionStart = productionStart;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductState getState() {
		return state;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setState(ProductState productState) {
		this.state = productState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getOrderNumber() {
		return orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setOrderNumber(int orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getStateDescription() {
		return stateDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStateDescription(String stateDescription) {
		this.stateDescription = stateDescription;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventQuery getTransitionToRunningState() {
		return transitionToRunningState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTransitionToRunningState(EventQuery eventQuery) {
		transitionToRunningState = eventQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventQuery getTransitionToWarningState() {
		return transitionToWarningState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTransitionToWarningState(EventQuery eventQuery) {
		transitionToWarningState = eventQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventQuery getTransitionToErrorState() {
		return transitionToErrorState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTransitionToErrorState(EventQuery eventQuery) {
		transitionToErrorState = eventQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventQuery getStatusUpdateQuery(ProductState newState) {
		switch (newState) {
			case RUNNING:
				return getTransitionToRunningState();
			case WARNING:
				return getTransitionToWarningState();
			case ERROR:
				return getTransitionToErrorState();
			default:
				return null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatusUpdateQuery(ProductState newState, EventQuery eventQuery) {
		switch (newState) {
			case RUNNING:
				setTransitionToRunningState(eventQuery);
				break;
			case WARNING:
				setTransitionToWarningState(eventQuery);
				break;
			case ERROR:
				setTransitionToErrorState(eventQuery);
				break;
			default:
				// empty, since there is nothing to do
				break;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getNumberOfDevices() {
		return numberOfDevices;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getNumberOfEvents() {
		return numberOfEvents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incrementNumberOfEvents(long count) {
		this.numberOfEvents += count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void incrementNumberOfDevices(long count) {
		this.numberOfDevices += count;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Set<ErrorType> getErrorTypes() {
		return errorTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ErrorType getErrorType(int causeCode) {
		for (ErrorType type : errorTypes) {
			if (type.getCauseCode() == causeCode) {
				return type;
			}
		}

		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setErrorTypes(Set<ErrorType> errorTypes) {
		this.errorTypes = errorTypes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addErrorType(ErrorType errorType) {
		errorTypes.add(errorType);
	}
}
