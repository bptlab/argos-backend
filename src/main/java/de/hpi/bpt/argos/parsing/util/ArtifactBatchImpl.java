package de.hpi.bpt.argos.parsing.util;

import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
import de.hpi.bpt.argos.util.performance.WatchImpl;
import de.hpi.bpt.argos.util.performance.WatchTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class ArtifactBatchImpl implements ArtifactBatch {
	private static final Logger logger = LoggerFactory.getLogger(ArtifactBatchImpl.class);

	private static final int BATCH_SIZE = 500;

	private WatchTask storageTask;
	private List<PersistenceArtifact> artifactsToStore;
	private long totalArtifactsStored;

	/**
	 * This constructor initializes all members with their default value.
	 */
	public ArtifactBatchImpl() {
		artifactsToStore = new ArrayList<>();
		totalArtifactsStored = 0;
		storageTask = WatchImpl.start("storing artifacts", () -> {});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(PersistenceArtifact... artifacts) {
		artifactsToStore.addAll(Arrays.asList(artifacts));
		executeStore(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		executeStore(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTotalArtifactsStored() {
		return totalArtifactsStored;
	}

	/**
	 * This method calls the store method and measures its time to execute
	 * @param finish - indicates whether all remaining artifacts should be saved
	 */
	private void executeStore(boolean finish) {
		storageTask.also(() -> store(finish));

		if (finish) {
			storageTask.finish();
		}
	}

		/**
		 * This method checks whether there are enough artifacts to store and, if so, stores them in the database.
		 * @param finish - indicates whether all remaining artifacts should be saved
		 */
	private void store(boolean finish) {
		while (!artifactsToStore.isEmpty() && (finish || artifactsToStore.size() >= BATCH_SIZE)) {
			List<PersistenceArtifact> artifacts = artifactsToStore.subList(0, Math.min(BATCH_SIZE, artifactsToStore.size()));
			PersistenceAdapterImpl.getInstance().saveArtifacts(artifacts.toArray(new PersistenceArtifact[artifacts.size()]));

			totalArtifactsStored += artifacts.size();

			logger.info(String.format("%1$d new artifacts stored -> %2$d stored in total", artifacts.size(), totalArtifactsStored));

			artifacts.clear();
		}
	}
}
