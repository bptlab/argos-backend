package de.hpi.bpt.argos.storage;


import de.hpi.bpt.argos.core.ArgosTestParent;
import de.hpi.bpt.argos.storage.dataModel.entity.Entity;
import de.hpi.bpt.argos.storage.dataModel.entity.VirtualRoot;
import de.hpi.bpt.argos.storage.dataModel.entity.type.EntityType;
import de.hpi.bpt.argos.storage.hierarchy.EntityHierarchyNode;
import de.hpi.bpt.argos.storage.hierarchy.HierarchyBuilderImpl;
import de.hpi.bpt.argos.testUtil.ArgosTestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

public class EntityHierarchyTest extends ArgosTestParent {

	@BeforeClass
	public static void initialize() {
		ArgosTestParent.setup();
	}

	@AfterClass
	public static void tearDown() {
		ArgosTestParent.tearDown();
	}

	@Test
	public void testGetEntityHierarchyRootNode() {
		EntityType testEntityType = ArgosTestUtil.createEntityType(true);
		Entity testRootEntity = ArgosTestUtil.createEntity(testEntityType, true);
		Entity testChildEntity1 = ArgosTestUtil.createEntity(testEntityType, testRootEntity, true);
		Entity testChildEntity2 = ArgosTestUtil.createEntity(testEntityType, testRootEntity, true);

		EntityHierarchyNode root = HierarchyBuilderImpl.getInstance().getEntityHierarchyRootNode();

		PersistenceAdapterImpl.getInstance().deleteArtifacts(testEntityType, testRootEntity, testChildEntity1, testChildEntity2);

		assertEquals(VirtualRoot.getInstance().getId(), root.getId());
		assertAllChildIdsContained(root, testRootEntity, testChildEntity1, testChildEntity2);

		EntityHierarchyNode rootEntity = root.findChildEntity(testRootEntity.getId());
		assertNotNull(rootEntity);
		assertAllChildIdsContained(rootEntity, testChildEntity1, testChildEntity2);

		EntityHierarchyNode childEntity1 = root.findChildEntity(testChildEntity1.getId());
		assertNotNull(childEntity1);
		assertHasNoChildren(childEntity1);

		EntityHierarchyNode childEntity2 = root.findChildEntity(testChildEntity2.getId());
		assertNotNull(childEntity2);
		assertHasNoChildren(childEntity2);
	}

	@Test
	public void testGetEntityHierarchyRootNode_EmptyHierarchy() {
		EntityHierarchyNode root = HierarchyBuilderImpl.getInstance().getEntityHierarchyRootNode();

		assertNotNull(root);
		assertHasNoChildren(root);
	}

	private void assertAllChildIdsContained(EntityHierarchyNode root, Entity... children) {
		List<Long> childIds = new ArrayList<>();
		childIds.add(root.getId());

		for (Entity child : children) {
			childIds.add(child.getId());
		}

		assertTrue(root.getChildIds().containsAll(childIds));
	}

	private void assertHasNoChildren(EntityHierarchyNode root) {
		List<Long> childIds = root.getChildIds();

		assertEquals(1, childIds.size());
		assertEquals(root.getId(), (long) childIds.get(0));
		assertFalse(root.hasChildren());
	}
}
