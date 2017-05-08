package de.hpi.bpt.argos.util;

import org.slf4j.Logger;

/**
 * This interface represents a common usable class for more logging convenience.
 */
public interface LoggerUtil {

	/**
	 * This method logs an errorMessage and the trace of an exception.
	 * @param logger - the logger to use for logging
	 * @param errorMessage - the errorMessage to log in error-mode
	 * @param throwable - the exception, which trace will be logged in trace-mode
	 */
	void error(Logger logger, String errorMessage, Throwable throwable);

	/**
	 * This method logs an infoMessage and the trace of an exception.
	 * @param logger - the logger to use for logging
	 * @param infoMessage - the infoMessage to log in info-mode
	 * @param throwable - the exception, which trace will be logged in trace-mode
	 */
	void info(Logger logger, String infoMessage, Throwable throwable);
}
