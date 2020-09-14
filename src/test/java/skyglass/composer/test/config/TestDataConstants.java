package skyglass.composer.test.config;

import skyglass.composer.stock.domain.model.UserInfo;

public interface TestDataConstants {

	public static final String TEST_TIMEZONE = "Asia/Tokyo";

	public static final String TEST_USER1_USERNAME = "USER1";

	static final UserInfo TEST_USER1 = new UserInfo("USER1", "12345!Test", "skyglass", "testuser1", "skyglass-testuser1@skyglass.net");

	static final UserInfo TEST_USER2 = new UserInfo("USER2", "12345!Test", "skyglass2", "testuser2", "skyglass-testuser2@skyglass.net");

	public static final String STOCK_CENTER_UUID = "158d60d5-5a81-4b1f-b7d6-36a349e05082";

	public static final String BUSINESSUNIT1_UUID = "DF789ACB-0CC3-4B4C-BF73-1E68DE4C7CA4";

	public static final String BUSINESSUNIT2_UUID = "d659dd95-c3b7-4f55-adf0-596a117c12b9";

	public static final String ITEM1_UUID = "9f797b73-ffbe-41c5-b7ed-453d450a7ef4";

	public static final String ITEM2_UUID = "34034833-b32b-40ad-928f-eef12c9dbe2c";

	public static UserInfo getTestUser1() {
		return TEST_USER1;
	}

}
