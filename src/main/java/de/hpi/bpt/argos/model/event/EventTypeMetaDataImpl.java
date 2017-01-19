package de.hpi.bpt.argos.model.event;

import com.google.gson.Gson;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventTypeMetaDataImpl implements EventTypeMetaData {
	protected static final Gson serializer = new Gson();

	protected int numberOfEvents;
	protected String name;

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

	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
