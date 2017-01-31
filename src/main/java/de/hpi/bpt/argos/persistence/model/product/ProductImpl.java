package de.hpi.bpt.argos.persistence.model.product;

import de.hpi.bpt.argos.persistence.database.DatabaseConnection;
import de.hpi.bpt.argos.persistence.model.event.data.Event;
import de.hpi.bpt.argos.persistence.model.event.EventImpl;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQuery;
import de.hpi.bpt.argos.persistence.model.event.EventSubscriptionQueryImpl;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "Product")
public class ProductImpl implements Product {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected int id;

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
	public int getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setId(int id) {
		this.id = id;
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
