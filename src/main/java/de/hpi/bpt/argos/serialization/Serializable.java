package de.hpi.bpt.argos.serialization;

@FunctionalInterface //remove this annotation when adding more methods
public interface Serializable {
	String toJson();
}
