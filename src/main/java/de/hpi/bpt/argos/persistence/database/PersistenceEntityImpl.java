package de.hpi.bpt.argos.persistence.database;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@MappedSuperclass
public abstract class PersistenceEntityImpl implements PersistenceEntity {

	@Id
	@GeneratedValue
	@Column(name = "Id")
	protected long id;

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
	public void setId(long id) {
		this.id = id;
	}
}
