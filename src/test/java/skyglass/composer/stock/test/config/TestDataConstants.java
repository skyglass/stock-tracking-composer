package skyglass.composer.stock.test.config;

import skyglass.composer.stock.domain.UserInfo;

public interface TestDataConstants {

	public static final String TEST_TIMEZONE = "Asia/Tokyo";

	public static final String TEST_USER_1_USERNAME = "USER1";

	static final UserInfo TEST_USER_1 = new UserInfo("USER1", "12345!Test", "skyglass", "testuser1", "skyglass-testuser1@skyglass.net");

	public static UserInfo getTestUser1() {
		return TEST_USER_1;
	}

}
