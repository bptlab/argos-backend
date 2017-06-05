package de.hpi.bpt.argos.util.threading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class BackgroundWorkerImpl<DataType> implements BackgroundWorker<DataType> {
	private static final Logger logger = LoggerFactory.getLogger(BackgroundWorkerImpl.class);

	private List<DataType> workload;
	private Consumer<DataType> action;
	private Thread backgroundThread;

	/**
	 * This constructor initializes all members with the given values.
	 * @param action - the action to perform
	 */
	public BackgroundWorkerImpl(Consumer<DataType> action) {
		workload = new ArrayList<>();
		this.action = action;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BackgroundWorkerImpl<DataType> addWorkload(DataType dataObject) {
		workload.add(dataObject);

		if (backgroundThread == null || !backgroundThread.isAlive()) {
			backgroundThread = new Thread(this::run);
			backgroundThread.start();
		}

		return this;
	}

	/**
	 * This method is executed in the context of the background thread.
	 */
	private void run() {
		while (!workload.isEmpty()) {
			DataType dataObject = workload.get(0);
			workload.remove(0);

			action.accept(dataObject);
		}
	}
}
