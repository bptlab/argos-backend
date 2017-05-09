package de.hpi.bpt.argos.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@inheritDoc}
 * This is an abstract base implementation.
 */
public abstract class ObservableImpl<Observer> implements Observable<Observer> {

	protected enum ObserverOrder {
		FIRST_IN_FIRST_OUT,
		FIRST_IN_LAST_OUT,
	}

	private List<Observer> observers;
	protected ObserverOrder insertStrategy;

	/**
	 * This constructor initializes all members with their default value.
	 */
	protected ObservableImpl() {
		observers = new ArrayList<>();
		insertStrategy = ObserverOrder.FIRST_IN_FIRST_OUT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void subscribe(Observer observer) {
		switch (insertStrategy) {
			case FIRST_IN_FIRST_OUT:
				observers.add(observer);
				break;

			case FIRST_IN_LAST_OUT:
				observers.add(0, observer);
				break;

			default:
				observers.add(observer);
				break;
		}
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
