package de.hpi.bpt.argos.model;

import de.hpi.bpt.argos.serialization.Serializable;

public interface EventAttribute extends Serializable {
	String getName();

	void setName(String name);

	String getType();

	void setType(String type);
}
