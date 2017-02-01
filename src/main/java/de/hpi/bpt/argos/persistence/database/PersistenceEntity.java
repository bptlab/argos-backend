package de.hpi.bpt.argos.persistence.database;

/**
 * This interface represents persistence entities, which are identifiable via a unique id.
 */
public interface PersistenceEntity {

	/**
	 * This method return the unique identifier for this entity.
	 * @return - the unique identifier fot this event
	 */
	long getId();

	/**
	 * This method sets the unique identifier for this entity.
	 * @param id - the unique identifier to be set
	 */
	void setId(long id);
}
