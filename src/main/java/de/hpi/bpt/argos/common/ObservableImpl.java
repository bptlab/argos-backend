package de.hpi.bpt.argos.common;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * {@inheritDoc}
 * This is an abstract base implementation.
 */
public abstract class ObservableImpl<Observer> implements Observable<Observer> {

	private Set<Observer> observers;

	/**
	 * This constructor initializes all members with their default value.
	 */
	protected ObservableImpl() {
		observers = new HashSet<>();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subscribe(Observer observer) {
		observers.add(observer);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void unscubscibe(Observer observer) {
		observers.remove(observer);
	}

	/**
	 * This method invokes a method for each observer.
	 * @param notifyMethod - the method to invoke for each observer
	 */
	protected void notifyObservers(Consumer<Observer> notifyMethod) {
		for (Observer observer : observers) {
			notifyMethod.accept(observer);
		}
	}
}
