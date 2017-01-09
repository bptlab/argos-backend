package de.hpi.bpt.argos.model;

import java.util.Set;

public interface EventType {
	int getNumberOfEvents();

	void setNumberOfEvents(int numberOfEvents);

	String getName();

	void setName(String name);

	int getId();

	void setId(int id);

	Set<EventAttribute> getAttributes();

	void setAttributes(Set<EventAttribute> attributes);
}
