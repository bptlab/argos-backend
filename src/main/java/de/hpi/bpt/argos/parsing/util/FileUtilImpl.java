package de.hpi.bpt.argos.parsing.util;

import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.util.DataFile;
import de.hpi.bpt.argos.storage.util.DataFileImpl;

import java.io.File;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class FileUtilImpl implements FileUtil {

	private static FileUtil instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private FileUtilImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static FileUtil getInstance() {
		if (instance == null) {
			instance = new FileUtilImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean wasModified(File file) {
		DataFile dataFile = PersistenceAdapterImpl.getInstance().getDataFile(file.getAbsolutePath());

		return dataFile == null || dataFile.getModificationTimestamp() != file.lastModified();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void modify(File file) {
		DataFile dataFile = PersistenceAdapterImpl.getInstance().getDataFile(file.getAbsolutePath());

		if (dataFile == null) {
			dataFile = new DataFileImpl();
			dataFile.setPath(file.getAbsolutePath());
		}

		dataFile.setModificationTimestamp(file.lastModified());

		PersistenceAdapterImpl.getInstance().saveArtifacts(dataFile);
	}
}
