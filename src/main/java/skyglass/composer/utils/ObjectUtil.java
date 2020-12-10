package skyglass.composer.utils;

import org.apache.commons.beanutils.PropertyUtils;

public class ObjectUtil {

	public static void setUuid(Object object, Object value) {
		try {
			PropertyUtils.setSimpleProperty(object, "uuid", value);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

}
