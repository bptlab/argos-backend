package de.hpi.bpt.argos.util;

/**
 * This interface represents a pair of values.
 */
public interface Pair<KeyType, ValueType> {

	/**
	 * This method returns the key of the pair.
	 * @return - the key
	 */
	KeyType getKey();

	/**
	 * This method returns the value of the pair.
	 * @return - the value
	 */
	ValueType getValue();
}
