package de.hpi.bpt.argos.storage.dataModel.entity.type;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifactImpl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
@Entity
@Table(name = "EntityType")
public class EntityTypeImpl extends PersistenceArtifactImpl implements EntityType {

	@Column(name = "ParentId")
	protected long parentId;

	@Column(name = "Name")
	protected String name = "";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getParentId() {
		return parentId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParentId(long parentId) {
		this.parentId = parentId;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		this.name = name;
	}
}
