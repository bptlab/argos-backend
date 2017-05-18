package de.hpi.bpt.argos.util.performance;

/**
 * {@inheritDoc}
 * This is a null implementation
 */
public class NullWatchTask implements WatchTask {

	/**
	 * This constructor executes the given task.
	 * @param task - the task to execute
	 */
	public NullWatchTask(Runnable task) {
		task.run();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Watch getWatch() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask executeTask(String description, Runnable task) {
		task.run();
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask run(Runnable task) {
		task.run();
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask andThen(String description, Runnable task) {
		task.run();
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public WatchTask getLatestTask() {
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getExecutionTimeInMs() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTotalExecutionTimeInMs() {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getResult(long totalExecutionTimeInMs, String indent) {
		return "";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		// empty, since nothing to do
	}
}
