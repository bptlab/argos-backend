package de.hpi.bpt.argos.util;

import org.slf4j.Logger;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class LoggerUtilImpl implements LoggerUtil {

	private static LoggerUtil instance;

	/**
	 * This constructor initializes all members with default values.
	 */
	private LoggerUtilImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static LoggerUtil getInstance() {
		if (instance == null) {
			instance = new LoggerUtilImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void error(Logger logger, String errorMessage, Throwable throwable) {
		logger.error(errorMessage);
		logTrace(logger, throwable);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void info(Logger logger, String infoMessage, Throwable throwable) {
		logger.info(infoMessage);
		logTrace(logger, throwable);
	}

	/**
	 * This method logs an exception trace.
	 * @param logger - the logger to use for logging
	 * @param throwable - the exception to log
	 */
	private void logTrace(Logger logger, Throwable throwable) {
		logger.trace("reason: ", throwable);
	}
}
