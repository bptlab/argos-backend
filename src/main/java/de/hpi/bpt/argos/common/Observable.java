package de.hpi.bpt.argos.common;

import java.util.function.Consumer;

/**
 * This interface represents observable objects.
 * @param <Observer> - the observer type
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
	void unsubscribe(Observer observer);

	/**
	 * This method invokes a method for each observer.
	 * @param notifyMethod - the method to invoke for each observer
	 */
	void notifyObservers(Consumer<Observer> notifyMethod);
}
