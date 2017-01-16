package de.hpi.bpt.argos.core;

public interface Argos {
	void run(int port, int numberOfThreads);

	void run();

	void shutdown();
}
