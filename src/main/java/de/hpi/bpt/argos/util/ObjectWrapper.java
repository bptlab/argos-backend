package de.hpi.bpt.argos.util;

/**
 * This object is used to wrap another object.
 * @param <ObjectType> - the type of the object to wrap
 */
public class ObjectWrapper<ObjectType> {

	private ObjectType value;

	/**
	 * This method returns the wrapper object.
	 * @return - the wrapper object
	 */
	public ObjectType get() {
		return value;
	}

	/**
	 * This method sets the wrapper object.
	 * @param value - the object to wrap
	 */
	public void set(ObjectType value) {
		this.value = value;
	}
}
