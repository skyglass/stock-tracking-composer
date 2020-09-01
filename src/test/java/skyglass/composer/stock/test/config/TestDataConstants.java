package skyglass.composer.stock.test.config;

import skyglass.composer.stock.domain.UserInfo;

public interface TestDataConstants {

	public static final String TEST_TIMEZONE = "Asia/Tokyo";

	public static final String TEST_USER_1_USERNAME = "USER1";

	static final UserInfo TEST_USER_1 = new UserInfo("USER1", "12345!Test", "skyglass", "testuser1", "skyglass-testuser1@skyglass.net");

	public static final String STOCK_CENTER_UUID = "STOCK_CENTER";

	public static final String BUSINESSUNIT1_UUID = "BusinessUnit1";

	public static final String BUSINESSUNIT2_UUID = "BusinessUnit2";

	public static final String ITEM1_UUID = "Item1";

	public static final String ITEM2_UUID = "Item2";

	public static UserInfo getTestUser1() {
		return TEST_USER_1;
	}

}
