package skyglass.composer.stock.domain.service;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.domain.model.BusinessOwner;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.entity.service.BusinessOwnerService;
import skyglass.composer.stock.entity.service.BusinessUnitService;
import skyglass.composer.stock.test.helper.BusinessOwnerTestHelper;
import skyglass.composer.stock.test.helper.BusinessUnitTestHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class BusinessUnitTest extends AbstractBaseTest {

	@Autowired
	private BusinessUnitService businessUnitService;

	@Autowired
	private BusinessOwnerService businessOwnerService;

	private BusinessUnitTestHelper businessUnitTestHelper;

	private BusinessOwnerTestHelper businessOwnerTestHelper;

	@Before
	public void init() {
		businessOwnerTestHelper = BusinessOwnerTestHelper.create(businessOwnerService);
		businessUnitTestHelper = BusinessUnitTestHelper.create(businessUnitService);
	}

	@Test
	public void testCreateBusinessUnit() {
		BusinessOwner businessOwner = businessOwnerTestHelper.create("business-owner1");
		Assert.assertNotNull(businessOwner.getUuid());
		Assert.assertEquals("business-owner1", businessOwner.getName());
		BusinessUnit businessUnit = businessUnitTestHelper.create("test1", businessOwner);
		Assert.assertNotNull(businessUnit.getUuid());
		Assert.assertEquals(businessOwner, businessUnit.getOwner());
		Assert.assertEquals("test1", businessUnit.getName());

		Collection<BusinessUnit> result = businessUnitService.find(null);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(businessUnit, result.iterator().next());

		result = businessUnitService.findAll(null);
		Assert.assertEquals(1, result.size());
		Assert.assertEquals(businessUnit, result.iterator().next());

	}

}
