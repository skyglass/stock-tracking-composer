package skyglass.composer.stock.domain;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.persistence.entity.UserEntity;
import skyglass.composer.stock.persistence.service.PermissionApi;
import skyglass.composer.stock.test.bean.MockHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class PermissionBeanTest extends AbstractBaseTest {

	@Autowired
	private MockHelper mockHelper;

	@Autowired
	private PermissionApi permissionService;

	@Test
	public void getCurrentUser() throws Exception {
		mockHelper.logout();
		UserEntity user = permissionService.getUserFromContext();
		Assert.assertTrue(user == null);
	}

}
