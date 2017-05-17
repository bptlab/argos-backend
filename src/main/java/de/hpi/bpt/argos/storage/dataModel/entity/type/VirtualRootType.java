package de.hpi.bpt.argos.storage.dataModel.entity.type;

/**
 * This class is the virtual root node of the entityType hierarchy.
 */
public final class VirtualRootType extends EntityTypeImpl {

	private static EntityType instance;

	/**
	 * This constructor initializes all members with their const values.
	 */
	private VirtualRootType() {
		id = -1;
		parentId = 0;
		name = "virtual root";
	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static EntityType getInstance() {
		if (instance == null) {
			instance = new VirtualRootType();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setParentId(long parentId) {
		// empty, since this should not be changed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		// empty, since this should not be changed
	}
}
