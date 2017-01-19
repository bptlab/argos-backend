package de.hpi.bpt.argos.model.event;

import com.google.gson.Gson;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventImpl implements Event {
	protected static final Gson serializer = new Gson();

	protected EventType type;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EventType getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setEventType(EventType type) {
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
