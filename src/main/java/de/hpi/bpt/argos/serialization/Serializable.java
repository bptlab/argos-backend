package de.hpi.bpt.argos.serialization;

@FunctionalInterface //remove this annotation when adding more methods
public interface Serializable {
	/**
	 * This method describes how the object can be serialized into json string format.
	 * @return - the object as a json string
	 */
	String toJson();
}
