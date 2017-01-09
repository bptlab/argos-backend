package de.hpi.bpt.argos.model;

import de.hpi.bpt.argos.serialization.Serializable;

import java.util.Set;

public interface EventType extends Serializable {
	int getNumberOfEvents();

	void setNumberOfEvents(int numberOfEvents);

	String getName();

	void setName(String name);

	int getId();

	void setId(int id);

	Set<EventAttribute> getAttributes();

	void setAttributes(Set<EventAttribute> attributes);
}
