package de.hpi.bpt.argos.common;

/**
 * This interface represents observable objects.
 * @param <Observer> - the class of the observers
 */
public interface Observable<Observer> {

	/**
	 * This method adds a new observer.
	 * @param observer - the observer, which want to be notified
	 */
	void subscribe(Observer observer);

	/**
	 * This method removes an observer.
	 * @param observer - the observer, which does no longer want to be notified
	 */
	void unscubscibe(Observer observer);
}
