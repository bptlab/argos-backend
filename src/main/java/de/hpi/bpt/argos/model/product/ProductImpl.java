package de.hpi.bpt.argos.model.product;

import com.google.gson.Gson;
import de.hpi.bpt.argos.model.event.Event;

import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ProductImpl implements Product {
	protected static final Gson serializer = new Gson();

	protected int id;
	protected Set<Event> events = new HashSet<>();
	protected ProductMetaData metaData;

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
	public Set<Event> getEvents() {
		return events;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEvents(Set<Event> events) {
		this.events = events;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ProductMetaData getMetaData() {
		return metaData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMetaData(ProductMetaData productMetaData) {
		this.metaData = metaData;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
