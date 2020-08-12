package skyglass.composer.stock.test.bean;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.UserInfo;
import skyglass.composer.stock.persistence.PermissionBean;
import skyglass.composer.stock.persistence.UserEntity;
import skyglass.composer.stock.test.config.TestDataConstants;

@Component
public class MockHelper {
	public static final String TEST_USER1 = TestDataConstants.TEST_USER_1_USERNAME;

	private static UserEntity currentUser;

	@Autowired
	private PermissionBean permissionBean;

	public static UserEntity getCurrentUser() {
		return currentUser;
	}

	public UserInfo getDefaultUser() {
		return TestDataConstants.TEST_USER_1;
	}

	public void setupDefault() {
		mockUser(TestDataConstants.TEST_USER_1_USERNAME);
	}

	public void mockDefaultUser() {
		mockUser(TestDataConstants.TEST_USER_1_USERNAME);
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
