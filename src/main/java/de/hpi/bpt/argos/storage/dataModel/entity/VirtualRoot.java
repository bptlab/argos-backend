package de.hpi.bpt.argos.storage.dataModel.entity;

/**
 * This is the virtual root node for the entity hierarchy.
 */
public final class VirtualRoot extends EntityImpl {

	private static Entity instance;

	/**
	 * This constructor initializes all members with their const values.
	 */
	private VirtualRoot() {
		id = -1;
		parentId = 0;
		name = "virtual root";
		typeId = -1;
		status = "";
	}

	/**
	 * This method returns the singleton instance of this class.
	 * @return - the singleton instance of this class
	 */
	public static Entity getInstance() {
		if (instance == null) {
			instance = new VirtualRoot();
		}

		return instance;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setName(String name) {
		// empty, since this should not be changed
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
	public void setTypeId(long typeId) {
		// empty, since this should not be changed
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setStatus(String status) {
		// empty, since this should not be changed
	}
}
