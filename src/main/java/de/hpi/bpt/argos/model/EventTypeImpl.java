package de.hpi.bpt.argos.model;

import java.util.HashSet;
import java.util.Set;

public class EventTypeImpl implements EventType {
	protected int numberOfEvents;
	protected String name;
	protected int id;
	protected Set<EventAttribute> attributes;

	public EventTypeImpl() {
		attributes = new HashSet<>();
	}

	@Override
	public int getNumberOfEvents() {
		return numberOfEvents;
	}

	@Override
	public void setNumberOfEvents(int numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	@Override
	public Set<EventAttribute> getAttributes() {
		return attributes;
	}

	@Override
	public void setAttributes(Set<EventAttribute> attributes) {
		this.attributes = attributes;
	}
}
