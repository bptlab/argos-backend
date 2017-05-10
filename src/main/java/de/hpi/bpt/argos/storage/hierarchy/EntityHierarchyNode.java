package de.hpi.bpt.argos.storage.hierarchy;

/**
 * This interface represents nodes in the entityHierarchy.
 */
public interface EntityHierarchyNode {

	/**
	 * This method returns the unique identifier for the represented entity.
	 * @return - the unique identifier for the represented entity
	 */
	long getId();

	/**
	 * This method adds a new child to this node.
	 * @param child - the child to add
	 */
	void addChild(EntityHierarchyNode child);

	/**
	 * This method indicates, whether the represented entity has more children.
	 * @return - true, if the represented entity has more children
	 */
	boolean hasChildren();

	/**
	 * This method searches for a specific entity.
	 * @param id - the id of the entity to find
	 * @return - the entityHierarchyNode, which represents the entity or null
	 */
	EntityHierarchyNode findChildEntity(long id);
}
