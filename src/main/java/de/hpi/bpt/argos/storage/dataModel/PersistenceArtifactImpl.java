package de.hpi.bpt.argos.storage.dataModel;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * {@inheritDoc}
 * This is the abstract implementation. It offers a common base class to inherit from.
 */
@MappedSuperclass
public abstract class PersistenceArtifactImpl implements PersistenceArtifact {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	protected long id;

	@Column(name = "CreationTimestamp")
	private long creationTimestamp = System.currentTimeMillis();

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getId() {
		return id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getCreationTimestamp() {
		return creationTimestamp;
	}
}
