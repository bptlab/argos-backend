package de.hpi.bpt.argos.util.performance;

import org.slf4j.Logger;

/**
 * This interface offers methods to measure the execution time of methods.
 */
public interface Watch {

	/**
	 * This method executes a given task as a sub task of the latest non-finished task.
	 * @param description - the description for the task to execute
	 * @param task - the task to execute
	 * @return - the watchTask, which might contain sub tasks.
	 */
	WatchTask executeTask(String description, Runnable task);

	/**
	 * This method executes a given task on the same execution level as the latest non-finished task.
	 * @param description - the description of the task to execute
	 * @param task - the task to execute
	 * @return - the watchTask, which might contain sub tasks.
	 */
	WatchTask andThen(String description, Runnable task);

	/**
	 * This method finishes the latest non-finished task.
	 */
	void finish();

	/**
	 * This method logs the result of the time measurement.
	 * @param logger - the logger to use for logging
	 */
	void logResult(Logger logger);
}
