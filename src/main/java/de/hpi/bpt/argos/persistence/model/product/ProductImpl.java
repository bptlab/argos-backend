package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.PersistenceEntityImpl;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQueryImpl;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import java.util.Date;

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
	protected ProductState state = ProductState.RUNNING;

	@Column(name = "Name")
	protected String name = "";

	@Column(name = "OrderNumber")
	protected int orderNumber = 0;

	@Column(name = "StateDescription")
	protected String stateDescription = "";

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = EventSubscriptionQueryImpl.class)
	protected EventSubscriptionQuery transitionToRunningState;

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = EventSubscriptionQueryImpl.class)
	protected EventSubscriptionQuery transitionToWarningState;

	@OneToOne(cascade = {CascadeType.ALL}, fetch = FetchType.LAZY, targetEntity = EventSubscriptionQueryImpl.class)
	protected EventSubscriptionQuery transitionToErrorState;

	@Column(name = "NumberOfDevices")
	protected long numberOfDevices = 0;

	@Column(name = "NumberOfEvents")
	protected long numberOfEvents = 0;


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
	public EventSubscriptionQuery getTransitionToRunningState() {
		return transitionToRunningState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTransitionToRunningState(EventSubscriptionQuery eventSubscriptionQuery) {
		transitionToRunningState = eventSubscriptionQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventSubscriptionQuery getTransitionToWarningState() {
		return transitionToWarningState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTransitionToWarningState(EventSubscriptionQuery eventSubscriptionQuery) {
		transitionToWarningState = eventSubscriptionQuery;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventSubscriptionQuery getTransitionToErrorState() {
		return transitionToErrorState;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setTransitionToErrorState(EventSubscriptionQuery eventSubscriptionQuery) {
		transitionToErrorState = eventSubscriptionQuery;
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
}
