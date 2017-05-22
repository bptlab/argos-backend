package de.hpi.bpt.argos.storage.hierarchy;

/**
 * This interface offers methods to build the entityHierarchy.
 */
@FunctionalInterface
public interface HierarchyBuilder {

	/**
	 * This method returns the root node of the entityHierarchy.
	 * @return - the root node of the entityHierarchy
	 */
	EntityHierarchyNode getEntityHierarchyRootNode();
}
