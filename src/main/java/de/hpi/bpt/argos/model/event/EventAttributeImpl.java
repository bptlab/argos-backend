package de.hpi.bpt.argos.model.event;

import com.google.gson.Gson;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EventAttributeImpl implements EventAttribute {
	protected static final Gson serializer = new Gson();

	protected String name;
	protected String type;

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
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setType(String type) {
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
