package de.hpi.bpt.argos.storage.hierarchy;

import de.hpi.bpt.argos.core.Argos;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.VirtualRoot;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class HierarchyBuilderImpl implements HierarchyBuilder {

	private EntityHierarchyNode rootNode;

	private static HierarchyBuilder instance;

	/**
	 * This constructor hides the default public one to implement the singleton pattern.
	 */
	private HierarchyBuilderImpl() {

	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static HierarchyBuilder getInstance() {
		if (instance == null) {
			instance = new HierarchyBuilderImpl();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityHierarchyNode getEntityHierarchy() {
		if (rootNode != null && !Argos.getTestMode()) {
			return rootNode;
		}

		List<Entity> entities = PersistenceAdapterImpl.getInstance().getEntities();
		rootNode = new EntityHierarchyNodeImpl(VirtualRoot.getInstance().getId());

		entities = addChildren(rootNode, entities);

		assert entities.isEmpty();

		return rootNode;
	}

	/**
	 * This method adds all child entities to a parent entity.
	 * @param parent - the parent
	 * @param listOfAllEntities - a list of all remaining entities
	 * @return - a list fo all remaining entities after they have been added to a parent node
	 */
	private List<Entity> addChildren(EntityHierarchyNode parent, List<Entity> listOfAllEntities) {
		if (listOfAllEntities.isEmpty()) {
			return listOfAllEntities;
		}

		List<Entity> entities = new ArrayList<>(listOfAllEntities);
		List<EntityHierarchyNode> children = new ArrayList<>();

		for (Iterator<Entity> it = entities.iterator(); it.hasNext();) {
			Entity child = it.next();
			if (child.getParentId() != parent.getId()) {
				continue;
			}
			EntityHierarchyNode newChild = new EntityHierarchyNodeImpl(child.getId());
			children.add(newChild);
			parent.addChild(newChild);
			it.remove();
		}

		for (EntityHierarchyNode child : children) {
			entities = addChildren(child, entities);
		}

		return entities;
	}
}
