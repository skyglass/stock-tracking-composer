package skyglass.composer.stock.domain.service;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.service.ContextService;
import skyglass.composer.security.entity.service.OwnerService;
import skyglass.composer.stock.test.helper.ContextTestHelper;
import skyglass.composer.stock.test.helper.OwnerTestHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class ContextTest extends AbstractBaseTest {

	@Autowired
	private ContextService contextService;

	@Autowired
	private OwnerService ownerService;

	private ContextTestHelper contextTestHelper;

	private OwnerTestHelper ownerTestHelper;

	@Before
	public void init() {
		ownerTestHelper = OwnerTestHelper.create(ownerService);
		contextTestHelper = ContextTestHelper.create(contextService);
	}

	@Test
	public void testCreateContext() {
		Owner owner = ownerTestHelper.create("owner1");
		Assert.assertNotNull(owner.getUuid());
		Assert.assertEquals("owner1", owner.getName());
		Context context = contextTestHelper.create("test1", owner);
		Assert.assertNotNull(context.getUuid());
		Assert.assertEquals(owner, context.getOwner());
		Assert.assertEquals("test1", context.getName());

		Collection<Context> result = contextService.find(null);
		Assert.assertEquals(4, result.size());
		Assert.assertEquals(context, result.iterator().next());

		result = contextService.findAll(null);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(context, result.iterator().next());

	}

}
