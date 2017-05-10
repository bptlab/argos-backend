package de.hpi.bpt.argos.storage.hierarchy;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EntityHierarchyNodeImpl implements EntityHierarchyNode {

	private long id;
	private List<EntityHierarchyNode> children;

	/**
	 * This constructor initializes the id member with the given parameter.
	 * @param id - the id to be set
	 */
	public EntityHierarchyNodeImpl(long id) {
		this.id = id;
		children = new ArrayList<>();
	}

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
	public void addChild(EntityHierarchyNode child) {
		children.add(child);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public EntityHierarchyNode findChildEntity(long id) {
		if (id == this.id) {
			return this;
		}

		for (EntityHierarchyNode child : children) {
			EntityHierarchyNode entity = child.findChildEntity(id);

			if (entity != null) {
				return entity;
			}
		}

		return null;
	}
}
