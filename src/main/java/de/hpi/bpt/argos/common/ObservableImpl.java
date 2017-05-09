package de.hpi.bpt.argos.common;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@inheritDoc}
 * This is an abstract base implementation.
 */
public class ObservableImpl<Observer> implements Observable<Observer> {

	public enum ObserverOrder {
		FIRST_IN_FIRST_OUT,
		FIRST_IN_LAST_OUT,
	}

	private List<Observer> observers;
	protected ObserverOrder insertStrategy;

	/**
	 * This constructor initializes all members with their default value.
	 */
	public ObservableImpl() {
		observers = new ArrayList<>();
		insertStrategy = ObserverOrder.FIRST_IN_FIRST_OUT;
	}

	/**
	 * This constructor initializes the insertStrategy member with the given parameters.
	 * @param insertStrategy - the insert strategy to use
	 */
	public ObservableImpl(ObserverOrder insertStrategy) {
		this();
		this.insertStrategy = insertStrategy;
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
	public void unsubscribe(Observer observer) {
		observers.remove(observer);
	}

	/**
	 * This method invokes a method for each observer.
	 * @param notifyMethod - the method to invoke for each observer
	 */
	public void notifyObservers(Consumer<Observer> notifyMethod) {
		for (Observer observer : observers) {
			notifyMethod.accept(observer);
		}
	}
}
