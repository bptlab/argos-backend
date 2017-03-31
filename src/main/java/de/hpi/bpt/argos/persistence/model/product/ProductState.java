package de.hpi.bpt.argos.persistence.model.product;

/**
 * This enum represents all the states a product can be in.
 */
public enum ProductState {
	RUNNING,
	WARNING,
	ERROR,
	UNDEFINED;

	/**
	 * This method checks whether another product state is worse than this state.
	 * @param otherState - the other state to compare
	 * @return true, if this state is worse than the other
	 */
	boolean isWorse(ProductState otherState) {

		switch (otherState) {
			case RUNNING:
				return this == WARNING || this == ERROR;

			case WARNING:
				return this == ERROR;

			case ERROR:
				return false;

			case UNDEFINED:
				return true;

			default:
				return false;
		}
	}
}
