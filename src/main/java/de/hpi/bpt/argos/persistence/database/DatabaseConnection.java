package de.hpi.bpt.argos.persistence.database;

import de.hpi.bpt.argos.persistence.model.event.Event;
import de.hpi.bpt.argos.persistence.model.event.type.EventType;
import de.hpi.bpt.argos.persistence.model.product.Product;
import de.hpi.bpt.argos.persistence.model.product.ProductFamily;

import java.util.List;
import java.util.Map;

/**
 * This interface represents data connections which are used to communicate with the data base.
 */
public interface DatabaseConnection extends PersistenceEntityRetriever {

	/**
	 * This method makes the database call to save a set of entities in the database server.
	 * @param entities - a list of entities to save
	 * @param <T> - the type of each entity
	 */
	<T> void saveEntities(T... entities);
}
