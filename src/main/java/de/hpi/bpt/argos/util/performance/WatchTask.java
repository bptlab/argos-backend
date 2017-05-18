package de.hpi.bpt.argos.util.performance;

/**
 * This interface represents task, which get measured by a watch object.
 */
public interface WatchTask {

	/**
	 * This method returns the watch this task belongs to.
	 * @return - the watch for this task
	 */
	Watch getWatch();

	/**
	 * This method executes a sub task of this task.
	 * @param description - the description of the sub task
	 * @param task - the sub task to execute
	 * @return - the watchTask, which might contain sub tasks
	 */
	WatchTask executeTask(String description, Runnable task);

	/**
	 * This method executes another task on the same execution level as this task.
	 * @param description - the description of the task
	 * @param task - the task to execute
	 * @return - the watchTask, which might contain sub tasks
	 */
	WatchTask andThen(String description, Runnable task);

	/**
	 * This method returns the latest non-finished task.
	 * @return - the latest non-finished task
	 */
	WatchTask getLatestTask();

	/**
	 * This method returns the description of this watchTask.
	 * @return - the description of this watchTask
	 */
	String getDescription();

	/**
	 * This method returns the execution time of this watchTask in ms.
	 * @return - the execution time of this watchTask in ms
	 */
	long executionTimeInMs();

	/**
	 * This method returns the total execution time in ms of this task and all of its sub tasks.
	 * @return - the total execution time in ms of this task and all of its sub tasks
	 */
	long getTotalExecutionTimeInMs();

	/**
	 * This method builds a string for the result logging.
	 * @param totalExecutionTimeInMs - the overall time of execution for the whole watch
	 * @param indent - the indent to use for the result string
	 * @return - a string for the result logging
	 */
	String getResult(long totalExecutionTimeInMs, String indent);

	/**
	 * This method finishes the current watchTask.
	 */
	void finish();
}
