package skyglass.composer.stock.domain;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.persistence.PermissionBean;
import skyglass.composer.stock.persistence.UserEntity;
import skyglass.composer.stock.test.bean.MockHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class PermissionBeanTest extends AbstractBaseTest {

	@Autowired
	private MockHelper mockHelper;

	@Autowired
	private PermissionBean permissionBean;

	@Test
	public void getCurrentUser() throws Exception {
		mockHelper.logout();
		UserEntity user = permissionBean.getUserFromContext();
		Assert.assertTrue(user == null);
	}

}
