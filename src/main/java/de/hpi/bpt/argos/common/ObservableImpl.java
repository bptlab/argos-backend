package de.hpi.bpt.argos.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@inheritDoc}
 * This is an abstract base implementation.
 */
public class ObservableImpl<Observer> implements Observable<Observer> {

	public enum ObserverCallOrder {
		FIRST_IN_FIRST_OUT,
		LAST_IN_FIRST_OUT,
	}

	private List<Observer> observers;
	protected ObserverCallOrder processingStrategy;

	/**
	 * This constructor initializes all members with their default value.
	 */
	public ObservableImpl() {
		observers = new ArrayList<>();
		processingStrategy = ObserverCallOrder.FIRST_IN_FIRST_OUT;
	}

	/**
	 * This constructor initializes the processingStrategy member with the given parameters.
	 * @param insertStrategy - the insert strategy to use
	 */
	public ObservableImpl(ObserverCallOrder insertStrategy) {
		this();
		this.processingStrategy = insertStrategy;
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
	public void unsubscribe(Observer observer) {
		observers.remove(observer);
	}

	/**
	 * This method invokes a method for each observer.
	 * @param notifyMethod - the method to invoke for each observer
	 */
	public void notifyObservers(Consumer<Observer> notifyMethod) {
		if (processingStrategy == ObserverCallOrder.FIRST_IN_FIRST_OUT) {
			for (Observer observer : observers) {
				notifyMethod.accept(observer);
			}
		} else if (processingStrategy == ObserverCallOrder.LAST_IN_FIRST_OUT) {
			for (int i = observers.size() - 1; i >= 0; i--) {
				notifyMethod.accept(observers.get(i));
			}
		}
	}
}
