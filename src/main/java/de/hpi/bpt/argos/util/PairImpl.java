package de.hpi.bpt.argos.util;

/**
 * {@inheritDoc}
 * This is the implementation
 */
public class PairImpl<KeyType, ValueType> implements Pair<KeyType, ValueType> {

	private KeyType key;
	private ValueType value;

	/**
	 * This constructor initializes the members according to the parameters.
	 * @param key - the key to be set
	 * @param value - the value to be set
	 */
	public PairImpl(KeyType key, ValueType value) {
		this.key = key;
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public KeyType getKey() {
		return key;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ValueType getValue() {
		return value;
	}
}
