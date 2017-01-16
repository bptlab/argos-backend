package de.hpi.bpt.argos.core;

/**
 * This interface is the Argos application and provides methods for Argos administration.
 */
public interface Argos {
    /**
     * This method starts the Argos application on the given port with a given number of threads.
     * @param port - port to be launched on as integer
     * @param numberOfThreads - number of threads to use to run Argos application as integer
     */
	void run(int port, int numberOfThreads);

    /**
     * This method starts the Argos application on a default port with a default number of threads.
     */
	void run();

    /**
     * This method shuts the Argos application down and frees the used port again.
     */
	void shutdown();
}
