package de.hpi.bpt.argos.model;

import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeImpl implements EventType {
	protected static final Gson serializer = new Gson();

	protected int numberOfEvents;
	protected String name;
	protected int id;
	protected Set<EventAttribute> attributes;

	/**
	 * This is the constructor. It initializes an empty attribute set.
	 */
	public EventTypeImpl() {
		attributes = new HashSet<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
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
	public Set<EventAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setAttributes(Set<EventAttribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
