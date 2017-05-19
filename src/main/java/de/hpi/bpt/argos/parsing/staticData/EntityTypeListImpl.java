package de.hpi.bpt.argos.parsing.staticData;

import de.hpi.bpt.argos.parsing.util.ArtifactBatch;
import de.hpi.bpt.argos.storage.PersistenceAdapterImpl;
import de.hpi.bpt.argos.storage.dataModel.attribute.type.TypeAttribute;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.dataModel.entity.type.VirtualRootType;
import de.hpi.bpt.argos.util.Pair;
import de.hpi.bpt.argos.util.PairImpl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@inheritDoc}
 * This is the implementation.
 */
public class EntityTypeListImpl implements EntityTypeList {

	private List<String> existingEntityTypes;
	private Map<String, String> entityTypeParentNames;
	private Map<String, EntityType> entityTypes;
	private Map<String, List<TypeAttribute>> entityTypeAttributes;
	private ArtifactBatch artifactBatch;

	/**
	 * This constructor initializes all members with their default value.
	 * @param artifactBatch - the artifactBatch to use for saving entityTypes
	 */
	public EntityTypeListImpl(ArtifactBatch artifactBatch) {
		this.artifactBatch = artifactBatch;
		entityTypeParentNames = new HashMap<>();
		entityTypes = new HashMap<>();
		entityTypeAttributes = new HashMap<>();

		existingEntityTypes = new ArrayList<>();

		for (EntityType existingEntityType : PersistenceAdapterImpl.getInstance().getEntityTypes()) {
			existingEntityTypes.add(existingEntityType.getName());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(EntityType newType, List<TypeAttribute> newTypeAttributes) {
		add(newType, newTypeAttributes, VirtualRootType.getInstance());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(EntityType newType, List<TypeAttribute> newTypeAttributes, EntityType parent) {
		if (existingEntityTypes.contains(newType.getName())) {
			return;
		}

		entityTypeParentNames.put(newType.getName(), parent.getName());
		entityTypes.put(newType.getName(), newType);
		entityTypeAttributes.put(newType.getName(), newTypeAttributes);

		artifactBatch.add(newType);
		artifactBatch.add(newTypeAttributes.toArray(new TypeAttribute[newTypeAttributes.size()]));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Pair<EntityType, List<TypeAttribute>> get(String typeName) {
		return new PairImpl<>(entityTypes.get(typeName), entityTypeAttributes.get(typeName));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean nameInUse(String name) {
		return existingEntityTypes.contains(name) || entityTypes.containsKey(name);
	}
}
