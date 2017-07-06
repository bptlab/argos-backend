package de.hpi.bpt.argos.util.threading;

import java.util.function.Consumer;

/**
 * This interface offers methods to add workload to a variable number of background workers.
 */
public interface BackgroundWorkerPool<DataType> {

	/**
	 * This method sets up the pool of background workers.
	 * @param workers - the amount of simultaneously workers
	 * @param action - the action, which should be performed by the workers
	 */
	void setup(int workers, Consumer<DataType> action);

	/**
	 * This method adds workload to a background worker from this pool.
	 * @param dataObject - the data object to process
	 */
	void addWorkload(DataType dataObject);
}
