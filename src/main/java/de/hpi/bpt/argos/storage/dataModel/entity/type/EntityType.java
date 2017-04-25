package de.hpi.bpt.argos.storage.dataModel.entity.type;

import de.hpi.bpt.argos.storage.dataModel.PersistenceArtifact;

/**
 * This interface represents the 'class' of entities. (e.g. Product, ProductFamily)
 */
public interface EntityType extends PersistenceArtifact {

	/**
	 * This method returns the parent entityType id of this entityType.
	 * @return - the parent entityType id of this entityType
	 */
	long getParentId();

	/**
	 * This method sets the id of the parent entityType of this entityType.
	 * @param parentId - the id of the parent entityType of this entityType to be set
	 */
	void setParentId(long parentId);

	/**
	 * This method returns the name of this entityType.
	 * @return - the name of this entityType
	 */
	String getName();

	/**
	 * This method sets the name of this entityType.
	 * @param name - the name of this entityType to be set.
	 */
	void setName(String name);
}
