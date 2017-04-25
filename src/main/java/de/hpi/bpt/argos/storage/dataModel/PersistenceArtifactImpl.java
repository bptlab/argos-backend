package de.hpi.bpt.argos.storage.dataModel;

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
	private long id;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getId() {
		return id;
	}
}
