package skyglass.composer.test.config;

import java.util.Date;

import skyglass.composer.utils.date.DateUtil;

public class TestDateUtil {

	public static Date parse(String dateString) {
		return DateUtil.parse(dateString, TestDataConstants.TEST_TIMEZONE);
	}

	public static Date parseDateTime(String dateString) {
		return DateUtil.parseDateTime(dateString, TestDataConstants.TEST_TIMEZONE);
	}

}
