package skyglass.composer.security.domain.service;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.service.ContextService;
import skyglass.composer.security.entity.service.OwnerService;
import skyglass.composer.stock.exceptions.BusinessRuleValidationException;
import skyglass.composer.stock.exceptions.ClientException;
import skyglass.composer.stock.test.helper.ContextTestHelper;
import skyglass.composer.stock.test.helper.OwnerTestHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;
import skyglass.composer.utils.AssertUtil;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class ContextTest extends AbstractBaseTest {

	@Autowired
	private ContextService contextService;

	@Autowired
	private OwnerService ownerService;

	private ContextTestHelper contextTestHelper;

	private OwnerTestHelper ownerTestHelper;

	private Owner owner;

	@Before
	public void init() {
		ownerTestHelper = OwnerTestHelper.create(ownerService);
		contextTestHelper = ContextTestHelper.create(contextService);
		owner = ownerTestHelper.create("owner1");
		Assert.assertNotNull(owner.getUuid());
		Assert.assertEquals("owner1", owner.getName());
	}

	@Test
	public void testCreateContext() {
		Context context = contextTestHelper.create("test1", owner);
		Assert.assertNotNull(context.getUuid());
		Assert.assertEquals(owner, context.getOwner());
		Assert.assertEquals("test1", context.getName());

		try {
			contextService.findByName(null, " ");
			Assert.fail("null or empty name is not allowed");
		} catch (ClientException e) {
			Assert.assertEquals(400, e.getRawStatusCode());
			Assert.assertEquals("400 BAD_REQUEST The given name can neither be null nor empty", e.getMessage());
		}

		Context result = contextService.findByName(null, "test1");
		Assert.assertEquals(context, result);
		Assert.assertEquals(context.getName(), result.getName());

		contextService.delete(result.getUuid());

		result = contextService.findByName(null, "test1");
		Assert.assertNull(result);
	}

	@Test
	public void testCreateContextWithParent() {
		int initSize = contextService.findAll().size();
		Context parent = contextTestHelper.create("parent", owner);
		Assert.assertEquals("parent", parent.getName());

		Context child = contextTestHelper.create("child", owner, parent);
		Assert.assertEquals("child", child.getName());
		Assert.assertEquals(parent, child.getParent());

		Context result = contextService.findByName(null, "child");
		Assert.assertNull(result);
		result = contextService.findByName(parent.getUuid(), "child");
		Assert.assertEquals(child.getName(), result.getName());

		List<Context> list = contextService.find(parent.getUuid());
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child", e.getName()));

		list = contextService.findAll(parent.getUuid());
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child", e.getName()));

		contextService.delete(parent.getUuid());

		result = contextService.findByName(null, "child");
		Assert.assertNull(result);

		result = contextService.findByName(null, "parent");
		Assert.assertNull(result);

		list = contextService.findAll();
		Assert.assertEquals(initSize, list.size());
	}

	@Test
	public void testCreatMultipleContextsWithParent() {
		int initSize = contextService.findAll().size();
		Context parent = contextTestHelper.create("parent", owner);
		Assert.assertEquals("parent", parent.getName());

		Context child = contextTestHelper.create("child", owner, parent);
		Assert.assertEquals("child", child.getName());
		Assert.assertEquals(parent, child.getParent());

		Context result = contextService.findByName(null, "child");
		Assert.assertNull(result);
		result = contextService.findByName(parent.getUuid(), "child");
		Assert.assertEquals(child.getName(), result.getName());

		List<Context> list = contextService.find(parent.getUuid());
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child", e.getName()));

		list = contextService.findAll(parent.getUuid());
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child", e.getName()));

		contextService.delete(parent.getUuid());

		result = contextService.findByName(null, "child");
		Assert.assertNull(result);

		result = contextService.findByName(null, "parent");
		Assert.assertNull(result);

		list = contextService.findAll();
		Assert.assertEquals(initSize, list.size());
	}

	@Test
	public void testCreatMultipleContextsWithGrandParent() {
		int initSize = contextService.findAll().size();
		Context parent1 = contextTestHelper.create("parent1", owner);
		Assert.assertEquals("parent1", parent1.getName());

		Context child1 = contextTestHelper.create("child1", owner, parent1);
		Assert.assertEquals("child1", child1.getName());
		Assert.assertEquals(parent1, child1.getParent());

		List<Context> created = contextTestHelper.create(owner, child1, "child11", "child12");
		AssertUtil.findAndTest("Context child not found", created,
				e -> Objects.equals("child11", e.getName()),
				e -> {
					Assert.assertEquals("child11", e.getName());
					Assert.assertEquals(child1, e.getParent());
				});
		AssertUtil.findAndTest("Context child not found", created,
				e -> Objects.equals("child12", e.getName()),
				e -> {
					Assert.assertEquals("child12", e.getName());
					Assert.assertEquals(child1, e.getParent());
				});

		Context child11 = contextService.findByName(child1.getUuid(), "child11");
		Assert.assertEquals("child11", child11.getName());
		Assert.assertEquals(child1, child11.getParent());

		Context child12 = contextService.findByName(child1.getUuid(), "child12");
		Assert.assertEquals("child12", child12.getName());
		Assert.assertEquals(child1, child12.getParent());

		Context child2 = contextTestHelper.create("child2", owner, parent1);
		Assert.assertEquals("child2", child2.getName());
		Assert.assertEquals(parent1, child2.getParent());

		created = contextTestHelper.createOrUpdate(owner, child1,
				Pair.of(child11.getUuid(), child11.getName() + "-updated"),
				Pair.of(child12.getUuid(), child12.getName() + "-updated"));

		AssertUtil.findAndTest("Context child not found", created,
				e -> Objects.equals("child11-updated", e.getName()),
				e -> {
					Assert.assertEquals("child11-updated", e.getName());
					Assert.assertEquals(child1, e.getParent());
				});
		AssertUtil.findAndTest("Context child not found", created,
				e -> Objects.equals("child12-updated", e.getName()),
				e -> {
					Assert.assertEquals("child12-updated", e.getName());
					Assert.assertEquals(child1, e.getParent());
				});

		child11 = contextService.findByName(child1.getUuid(), "child11-updated");
		Assert.assertEquals("child11-updated", child11.getName());
		Assert.assertEquals(child1, child11.getParent());

		child12 = contextService.findByName(child1.getUuid(), "child12-updated");
		Assert.assertEquals("child12-updated", child12.getName());
		Assert.assertEquals(child1, child12.getParent());

		created = contextTestHelper.create(owner, child2, "child21");
		AssertUtil.findAndTest("Context child not found", created,
				e -> Objects.equals("child21", e.getName()),
				e -> {
					Assert.assertEquals("child21", e.getName());
					Assert.assertEquals(child2, e.getParent());
				});

		Context child21 = contextService.findByName(child2.getUuid(), "child21");
		Assert.assertEquals("child21", child21.getName());
		Assert.assertEquals(child2, child21.getParent());

		try {
			contextTestHelper.createOrUpdate(owner, child1,
					Pair.of(child11.getUuid(), child11.getName()),
					Pair.of(child21.getUuid(), child21.getName()));
			Assert.fail("Should faiil here");
		} catch (BusinessRuleValidationException e) {
			Assert.assertEquals("400 BAD_REQUEST Context Update Error: Context parent can not be changed (name = child21)", e.getMessage());
		}

		contextTestHelper.createOrUpdate(owner, null,
				Pair.of(parent1.getUuid(), parent1.getName()));

		List<Context> list = contextService.find(parent1.getUuid());
		Assert.assertEquals(2, list.size());
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child1", e.getName()));
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child2", e.getName()));

		list = contextService.findAll(parent1.getUuid());
		Assert.assertEquals(5, list.size());
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child1", e.getName()));
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child2", e.getName()));
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child11-updated", e.getName()));
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child12-updated", e.getName()));
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child21", e.getName()));

		contextService.deleteAll(child1.getUuid());
		list = contextService.findAll(parent1.getUuid());
		Assert.assertEquals(2, list.size());
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child2", e.getName()));
		AssertUtil.found("Context child not found", list, e -> Objects.equals("child21", e.getName()));

		contextService.deleteAll(child2.getUuid());
		list = contextService.findAll(parent1.getUuid());
		Assert.assertEquals(0, list.size());

		contextService.deleteAll(parent1.getUuid());
		list = contextService.findAll();
		Assert.assertEquals(initSize, list.size());
	}

}
