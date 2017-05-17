package de.hpi.bpt.argos.parsing.staticData;

import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.util.Pair;

import java.util.List;

/**
 * This interface represents a list of entityTypes, which were generated in the current parsing process.
 */
public interface EntityTypeList {

	/**
	 * This method adds a new entityType to the list.
	 * @param newType - the new type to add to the list (has no explicit parent)
	 * @param newTypeAttributes - the attributes of the new type
	 */
	void add(EntityType newType, List<TypeAttribute> newTypeAttributes);

	/**
	 * This method adds a new entityType to the list.
	 * @param newType - the new type to add to the list (has an explicit parent)
	 * @param newTypeAttributes - the attributes of the new type
	 * @param parent - the parent entityType
	 */
	void add(EntityType newType, List<TypeAttribute> newTypeAttributes, EntityType parent);

	/**
	 * This method returns a tuple of entityType and all of its typeAttributes.
	 * @param typeName - the name of the entityType to return
	 * @return - a tuple of entityType and all of its typeAttributes
	 */
	Pair<EntityType, List<TypeAttribute>> get(String typeName);
}
