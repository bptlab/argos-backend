package de.hpi.bpt.argos.util.threading;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class BackgroundWorkerPoolImpl<DataType> implements BackgroundWorkerPool<DataType> {

	private List<BackgroundWorker<DataType>> workers;
	private int nextWorker;

	/**
	 * This constructor initializes all members with their default values.
	 */
	public BackgroundWorkerPoolImpl() {
		workers = new ArrayList<>();
		nextWorker = 0;
	}

	/**
	 * This method creates a new backgroundWorkerPool.
	 * @param workers - the amount of workers in the pool
	 * @param action - the action, which should be performed by the workers
	 * @param <DataType> - the dataType of the data objects, which will be processed by the workers
	 * @return - the new backgroundWorkerPool
	 */
	public static <DataType> BackgroundWorkerPool<DataType> create(int workers, Consumer<DataType> action) {
		BackgroundWorkerPool<DataType> pool = new BackgroundWorkerPoolImpl<>();
		pool.setup(workers, action);

		return pool;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setup(int workers, Consumer<DataType> action) {
		for (int i = 0; i < workers; i++) {
			this.workers.add(new BackgroundWorkerImpl<>(action));
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addWorkload(DataType dataObject) {
		if (workers.isEmpty()) {
			return;
		}

		workers.get((nextWorker++) % workers.size()).addWorkload(dataObject);
	}
}
