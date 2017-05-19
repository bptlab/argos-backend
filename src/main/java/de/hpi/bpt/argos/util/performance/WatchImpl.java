package de.hpi.bpt.argos.util.performance;

import de.hpi.bpt.argos.core.Argos;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public final class WatchImpl implements Watch {

	private static Map<Long, Watch> watches = new HashMap<>();
	private List<WatchTask> tasks;

	/**
	 * This constructor initializes all members with their default values.
	 */
	private WatchImpl() {
		tasks = new ArrayList<>();
	}

	/**
	 * This method returns the watch, which is responsible for the current thread.
	 * @return - the watch, which is responsible for the current thread
	 */
	private static Watch getWatch() {
		if (!watches.containsKey(Thread.currentThread().getId())) {
			watches.put(Thread.currentThread().getId(), new WatchImpl());
		}

		return watches.get(Thread.currentThread().getId());
	}

	/**
	 * This method executes a task as a sub task of the latest non-finished task.
	 * @param description - the description for the task
	 * @param task - the task to perform
	 * @return - the watchTask after its execution
	 */
	public static WatchTask start(String description, Runnable task) {
		if (!Argos.shouldMeasurePerformance()) {
			return new NullWatchTask(task);
		}

		return getWatch().executeTask(description, task);
	}

	/**
	 * This method executes a task on the same execution level as the latest non-finished task.
	 * @param description - the description of the task
	 * @param task - the task to perform
	 * @return - the watchTask after its execution
	 */
	public static WatchTask then(String description, Runnable task) {
		if (!Argos.shouldMeasurePerformance()) {
			return new NullWatchTask(task);
		}

		return getWatch().andThen(description, task);
	}

	/**
	 * This method stops the time measurement for the currently active task.
	 */
	public static void stop() {
		if (!Argos.shouldMeasurePerformance()) {
			return;
		}

		getWatch().finish();
	}

	/**
	 * This method measures the execution time of a single method.
	 * @param description - the description of the task
	 * @param task - the task to perform
	 */
	public static void measure(String description, Runnable task) {
		start(description, task);
		stop();
	}

	/**
	 * This method logs the result of the watch, which is responsible for this thread.
	 * @param logger - the logger to use
	 */
	public static void printResult(Logger logger) {
		if (!Argos.shouldMeasurePerformance()) {
			return;
		}

		stop();
		getWatch().logResult(logger);
		watches.remove(Thread.currentThread().getId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask executeTask(String description, Runnable task) {
		WatchTask latestTask = getLatestTask();

		if (latestTask == null) {
			latestTask = new WatchTaskImpl(this, description);
			tasks.add(latestTask);
			latestTask.run(task);
		} else {
			latestTask = latestTask.executeTask(description, task);
		}

		return latestTask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask andThen(String description, Runnable task) {
		WatchTask latestTask = getLatestTask();

		if (latestTask == null) {
			latestTask = new WatchTaskImpl(this, description);
			tasks.add(latestTask);
			latestTask.run(task);
		} else {
			latestTask = latestTask.andThen(description, task);
		}

		return latestTask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		WatchTask latestTask = getLatestTask();

		if (latestTask != null) {
			latestTask.finish();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void logResult(Logger logger) {
		long totalExecutionTime = 0;

		for (WatchTask task : tasks) {
			totalExecutionTime += task.getTotalExecutionTimeInMs();
		}

		StringBuilder result = new StringBuilder();
		result.append(String.format("execution times: total %1$d ms%n", totalExecutionTime));

		for (WatchTask task : tasks) {
			result.append(task.getResult(totalExecutionTime, "    "));
		}

		logger.info(result.toString());
	}

	/**
	 * This method returns the latest non-finished task.
	 * @return - the latest non-finished task or null
	 */
	private WatchTask getLatestTask() {
		WatchTask latestTask = null;

		for (WatchTask subTask : tasks) {
			latestTask = subTask.getLatestTask();

			if (latestTask != null) {
				break;
			}
		}

		return latestTask;
	}
}
