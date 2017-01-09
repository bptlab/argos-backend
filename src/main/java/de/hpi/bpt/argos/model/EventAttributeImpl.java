package de.hpi.bpt.argos.model;

import com.google.gson.Gson;

public class EventAttributeImpl implements EventAttribute {
	protected static final Gson serializer = new Gson();

	protected String name;
	protected String type;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getType() {
		return type;
	}

	@Override
	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toJson() {
		return serializer.toJson(this);
	}
}
