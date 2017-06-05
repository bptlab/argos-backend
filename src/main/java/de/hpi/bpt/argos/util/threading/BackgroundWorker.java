package de.hpi.bpt.argos.util.threading;

/**
 * This interface offers methods to execute tasks async.
 */
@FunctionalInterface
public interface BackgroundWorker<DataType> {

	/**
	 * This method adds new data to the workload of this backgroundWorker.
	 * @param dataObject - the data which should get processed
	 * @return - this backgroundWorker object
	 */
	BackgroundWorkerImpl<DataType> addWorkload(DataType dataObject);
}
