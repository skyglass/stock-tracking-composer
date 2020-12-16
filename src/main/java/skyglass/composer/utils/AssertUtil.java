package skyglass.composer.utils;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import skyglass.composer.stock.exceptions.BusinessRuleValidationException;
import skyglass.composer.stock.exceptions.NotNullableNorEmptyException;

public class AssertUtil {

	public static void notNull(String message, Object... objects) {
		notNull(null, message, objects);
	}

	public static void notNull(Class<? extends Serializable> type, String message, Object... objects) {
		for (Object o : objects) {
			if (o == null) {
				throw new NotNullableNorEmptyException(type, message);
			}
		}
	}

	public static void notEmpty(String message, String... fields) {
		notEmpty(null, message, fields);
	}

	public static void notEmpty(Class<? extends Serializable> type, String message, String... fields) {
		for (String f : fields) {
			if (StringUtils.isBlank(f)) {
				throw new NotNullableNorEmptyException(type, message);
			}
		}
	}

	public static void notEmpty(String message, Collection<?>... collections) {
		notEmpty(null, message, collections);
	}

	public static void notEmpty(Class<? extends Serializable> type, String message, Collection<?>... collections) {
		for (Collection<?> c : collections) {
			if (CollectionUtils.isEmpty(c)) {
				throw new NotNullableNorEmptyException(type, message);
			}
		}
	}

	public static void isTrue(String message, Boolean... expressions) {
		for (Boolean b : expressions) {
			if (!b) {
				throw new BusinessRuleValidationException(message);
			}
		}
	}

	@SafeVarargs
	public static void isTrue(String message, Supplier<Boolean>... suppliers) {
		for (Supplier<Boolean> s : suppliers) {
			if (!s.get()) {
				throw new BusinessRuleValidationException(message);
			}
		}
	}

	@SafeVarargs
	public static <T> void found(String message, List<T> list, Predicate<T>... predicates) {
		boolean found = false;
		for (T element : list) {
			for (Predicate<T> predicate : predicates) {
				if (!predicate.test(element)) {
					continue;
				}
			}
			found = true;
			break;
		}
		if (!found) {
			throw new BusinessRuleValidationException(message);
		}
	}

}
