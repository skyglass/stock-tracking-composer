package skyglass.composer.security.domain.service;

import java.util.List;
import java.util.Objects;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.service.ContextService;
import skyglass.composer.security.entity.service.OwnerService;
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

}
