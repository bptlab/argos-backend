package de.hpi.bpt.argos.parsing.util;

import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;
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

	private static final int BATCH_SIZE = 50;

	private List<PersistenceArtifact> artifactsToStore;
	private long totalArtifactsStored;

	/**
	 * This constructor initializes all members with their default value.
	 */
	public ArtifactBatchImpl() {
		artifactsToStore = new ArrayList<>();
		totalArtifactsStored = 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(PersistenceArtifact... artifacts) {
		artifactsToStore.addAll(Arrays.asList(artifacts));
		store(false);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void finish() {
		store(true);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getTotalArtifactsStored() {
		return totalArtifactsStored;
	}

	/**
	 * This method checks whether there are enough artifacts to store and, if so, stores them in the database.
	 * @param finish - indicates whether all remaining artifacts should be saved
	 */
	private void store(boolean finish) {
		while (finish || artifactsToStore.size() >= BATCH_SIZE) {
			List<PersistenceArtifact> artifacts = new ArrayList<>(artifactsToStore.subList(0, Math.max(BATCH_SIZE, artifactsToStore.size())));
			PersistenceAdapterImpl.getInstance().saveArtifacts(artifacts.toArray(new PersistenceArtifact[artifacts.size()]));

			totalArtifactsStored += artifacts.size();

			logger.info(String.format("%1$d new artifacts -> %2$d in total", artifacts.size(), totalArtifactsStored));

			artifacts.clear();
		}
	}
}
