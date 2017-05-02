package de.hpi.bpt.argos.storage.util;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "DataFile")
public class DataFileImpl extends PersistenceArtifactImpl implements DataFile {

	@Column(name = "Path")
	String path = "";

	@Column(name = "ModificationTimestamp")
	long modificationTimestamp = 0;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setPath(String path) {
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getModificationTimestamp() {
		return modificationTimestamp;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setModificationTimestamp(long modificationTimestamp) {
		this.modificationTimestamp = modificationTimestamp;
	}
}
