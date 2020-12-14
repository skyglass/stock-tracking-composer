package skyglass.composer.stock.test.bean;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.security.domain.model.UserInfo;
import skyglass.composer.security.entity.model.UserEntity;
import skyglass.composer.security.entity.repository.PermissionBean;
import skyglass.composer.test.config.TestDataConstants;

@Component
public class MockHelper {
	public static final String TEST_USER1 = TestDataConstants.TEST_USER1_USERNAME;

	private static UserEntity currentUser;

	@Autowired
	private PermissionBean permissionBean;

	public static UserEntity getCurrentUser() {
		return currentUser;
	}

	public UserInfo getDefaultUser() {
		return TestDataConstants.TEST_USER1;
	}

	public void setupDefault() {
		mockUser(TestDataConstants.TEST_USER1_USERNAME);
	}

	public void mockDefaultUser() {
		mockUser(TestDataConstants.TEST_USER1_USERNAME);
	}

	public void logout() {
		Mockito.doReturn(null).when(permissionBean).getUserFromContext();
		Mockito.doReturn(null).when(permissionBean).getUsernameFromContext();
	}

	public void mockUser(String username) {
		currentUser = permissionBean.getUser(username);
		Mockito.doReturn(currentUser).when(permissionBean).getUserFromContext();
		Mockito.doReturn(username).when(permissionBean).getUsernameFromContext();
	}

}
