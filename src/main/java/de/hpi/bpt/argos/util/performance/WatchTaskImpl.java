package de.hpi.bpt.argos.util.performance;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class WatchTaskImpl implements WatchTask {

	private static final double TO_PERCENT = 100;

	private WatchTask parentTask;
	private Watch parentWatch;
	private List<WatchTask> subTasks;
	private long executionTime;
	private String description;
	private boolean finished;

	/**
	 * This constructor creates a new watchTask on first execution level.
	 * @param parentWatch - the parent watch of this task
	 * @param description - the description of the task to execute
	 * @param task - the task to perform
	 */
	public WatchTaskImpl(Watch parentWatch, String description, Runnable task) {
		this.parentWatch = parentWatch;
		subTasks = new ArrayList<>();
		this.description = description;
		finished = false;

		run(task);
	}

	/**
	 * This constructor creates a new watchTask on a sub execution level.
	 * @param parentWatch - the parent watch of this task
	 * @param parentTask - the parent task of this task
	 * @param description - the description of the task to execute
	 * @param task - the task to perform
	 */
	public WatchTaskImpl(Watch parentWatch, WatchTask parentTask, String description, Runnable task) {
		this(parentWatch, description, task);
		this.parentTask = parentTask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Watch getWatch() {
		return parentWatch;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask executeTask(String description, Runnable task) {
		WatchTask subTask = new WatchTaskImpl(parentWatch, this, description, task);
		subTasks.add(subTask);

		return subTask;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask also(Runnable task) {
		run(task);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask andThen(String description, Runnable task) {
		finish();

		if (parentTask != null) {
			return parentTask.executeTask(description, task);
		} else {
			return parentWatch.executeTask(description, task);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long executionTimeInMs() {
		return executionTime;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTotalExecutionTimeInMs() {
		long time = executionTime;

		for (WatchTask subTask : subTasks) {
			time += subTask.getTotalExecutionTimeInMs();
		}

		return time;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResult(long totalExecutionTimeInMs, String indent) {
		StringBuilder result = new StringBuilder();

		long subTime = getTotalExecutionTimeInMs();
		result.append(String.format("%1$s- %2$s in %3$d ms (%4$,.2f %%)%n",
				indent,
				description,
				subTime,
				((double) subTime / Math.max(1, totalExecutionTimeInMs)) * TO_PERCENT));

		for (WatchTask subTask : subTasks) {
			result.append(subTask.getResult(totalExecutionTimeInMs, indent + "    "));
		}

		return result.toString();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask getLatestTask() {
		if (finished) {
			return null;
		}

		for (WatchTask subTask : subTasks) {
			WatchTask task = subTask.getLatestTask();

			if (task != null) {
				return task;
			}
		}

		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		finished = true;
	}

	/**
	 * This method executes a task and adds the execution time to the local member.
	 * @param task - the task to execute
	 */
	private void run(Runnable task) {
		long ms = System.currentTimeMillis();
		task.run();
		executionTime += System.currentTimeMillis() - ms;
	}
}
