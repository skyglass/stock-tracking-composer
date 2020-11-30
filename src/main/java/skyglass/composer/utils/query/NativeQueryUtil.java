package skyglass.composer.utils.query;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.persistence.Query;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.codecs.AbstractCharacterCodec;
import org.owasp.esapi.codecs.Codec;
import org.owasp.esapi.reference.DefaultEncoder;

import skyglass.composer.stock.domain.model.IdObject;
import skyglass.composer.utils.EnumUtil;
import skyglass.composer.utils.query.builder.QueryRequestDTO;

public class NativeQueryUtil {

	private static final Encoder DEFAULT_ENCODER = DefaultEncoder.getInstance();

	@SuppressWarnings("rawtypes")
	private static final Codec DEFAULT_DB_CODEC = new AbstractCharacterCodec() {
	};

	public static String getEncoded(String plainInput) {
		return DEFAULT_ENCODER.encodeForSQL(DEFAULT_DB_CODEC, plainInput);
	}

	public static <T> String getEncodedInString(Function<T, String> function, Collection<T> list) {
		if (function == null) {
			return "(" + list.stream().map(s -> "'" + getEncoded(s.toString()) + "'").collect(Collectors.joining(", ")) + ")";
		}
		return "(" + list.stream().map(s -> "'" + getEncoded(function.apply(s)) + "'").collect(Collectors.joining(", ")) + ")";
	}

	public static <T> String getEncodedInString(Collection<T> list) {
		return getEncodedInString(null, list);
	}

	public static String getOrList(String propertyPath, List<String> params) {
		return String.format("(%s = '", propertyPath) + String.join(String.format("' OR %s = '", propertyPath), params)
				+ "')";
	}

	public static <T> String getInString(Function<T, String> function, Collection<T> list) {
		if (function == null) {
			return "(" + list.stream().map(s -> "'" + s.toString() + "'").collect(Collectors.joining(", ")) + ")";
		}
		return "(" + list.stream().map(s -> "'" + function.apply(s) + "'").collect(Collectors.joining(", ")) + ")";
	}

	public static <T> String getInString(Collection<T> list) {
		return getInString(null, list);
	}

	public static void setInQueryParamValues(List<String> values, Query query) {
		setInQueryParamValues("p", values, query);
	}

	public static void setInQueryParamValues(String prefix, List<String> values, Query query) {
		if (CollectionUtils.isEmpty(values)) {
			return;
		}
		IntStream.range(0, values.size()).forEach(i -> query.setParameter(prefix + (i + 1), values.get(i)));
	}

	public static String getInQueryParams(int size) {
		return getInQueryParams("p", size);
	}

	public static String getInQueryParams(String prefix, int size) {
		return "(" + IntStream.rangeClosed(1, size).mapToObj(i -> "?" + prefix + i).collect(Collectors.joining(",")) + ")";
	}

	public static <T> String getNullableInString(String path, Collection<T> list) {
		return getNullableInString(path, list, "AND ");
	}

	public static <T> String getNullableInString(String path, Collection<T> list, String separator) {
		return CollectionUtils.isEmpty(list) ? "" : (separator + path + " IN " + getInString(list) + " ");
	}

	public static double getDoubleValueSafely(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof Double) {
			return (double) object;
		}
		try {
			return Double.parseDouble(object.toString());
		} catch (NumberFormatException ex) {
			return 0.0D;
		}
	}

	public static int getIntValueSafely(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof Integer) {
			return (int) object;
		}
		if (object instanceof Double) {
			return ((Double) object).intValue();
		}
		if (object instanceof Long) {
			return ((Long) object).intValue();
		}
		if (object instanceof BigDecimal) {
			return ((BigDecimal) object).intValue();
		}
		try {
			return Integer.parseInt(object.toString());
		} catch (NumberFormatException ex) {
			return 0;
		}
	}

	public static String getStringValueSafely(Object object) {
		if (object == null) {
			return "";
		}
		if (object instanceof Integer) {
			return String.valueOf((Integer) object);
		}
		return (String) object;
	}

	public static float getFloatValueSafely(Object object) {
		if (object == null) {
			return 0;
		}
		if (object instanceof BigDecimal) {
			return ((BigDecimal) object).floatValue();
		}
		return (float) object;
	}

	public static long getLongValueSafely(Object object) {
		if (object == null) {
			return 0L;
		}
		if (object instanceof Long) {
			return (long) object;
		}
		try {
			return Long.parseLong(object.toString());
		} catch (NumberFormatException ex) {
			return 0L;
		}
	}

	public static <DTO> List<DTO> buildDtoListFromSelectFields(Supplier<DTO> dtoSupplier, Collection<Object[]> queryResult, String selectString) {
		List<DTO> dtoList = new ArrayList<>();
		for (Object[] result : queryResult) {
			dtoList.add(buildDtoFromSelectFields(dtoSupplier, result, selectString));
		}
		return dtoList;
	}

	public static <DTO> DTO buildDtoFromSelectFields(Supplier<DTO> dtoSupplier, Object[] queryResult, String selectString) {
		DTO dto = dtoSupplier.get();
		int i = 0;
		for (String selectAlias : parseSelect(selectString)) {
			Object propValue = queryResult[i];
			if (selectAlias.equalsIgnoreCase("uuid") && dto instanceof IdObject && propValue != null) {
				//TODO: enable uuid setting
				//IdObject idObject = (IdObject) dto;
				//idObject.setUuid(propValue.toString());
			} else {
				try {
					Field typeField = dto.getClass().getDeclaredField(selectAlias);
					@SuppressWarnings("rawtypes")
					Class typeClass = typeField.getType();
					if (typeClass.isEnum() && propValue != null) {
						propValue = EnumUtil.getEnumInstanceObject(propValue, typeClass);
					}
					if (propValue instanceof BigInteger && propValue != null) {
						propValue = ((BigInteger) propValue).intValue();
					}
					if (propValue instanceof Long && propValue != null && isInteger(typeClass)) {
						propValue = ((Long) propValue).intValue();
					}
					if (isInteger(typeClass) && propValue == null) {
						propValue = 0;
					}
					if (isInteger(typeClass) && propValue instanceof String) {
						propValue = getIntValueSafely(propValue);
					}
					if (isDouble(typeClass) && propValue == null) {
						propValue = 0D;
					}
					if (isDouble(typeClass) && propValue instanceof String) {
						propValue = getDoubleValueSafely(propValue);
					}
					if (propValue != null) {
						PropertyUtils.setSimpleProperty(dto, selectAlias, propValue);
					}
				} catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | NoSuchFieldException ex) {
					throw new IllegalArgumentException("Could not set value of the property " + selectAlias + " to " + propValue, ex);
				}
			}
			i++;
		}
		return dto;
	}

	private static boolean isInteger(Class<?> typeClass) {
		return typeClass == Integer.class || typeClass == int.class;
	}

	private static boolean isDouble(Class<?> typeClass) {
		return typeClass == Double.class || typeClass == double.class;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, V> convertToMap(Collection<Object[]> queryResult) {
		if (CollectionUtils.isEmpty(queryResult)) {
			return Collections.emptyMap();
		}
		Map<K, V> result = new HashMap<>();
		for (Object[] row : queryResult) {
			K key = (K) row[0];
			V value = (V) row[1];
			result.put(key, value);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, K2, V> Map<K, Map<K2, V>> convertToMapOfMaps(Collection<Object[]> queryResult) {
		if (CollectionUtils.isEmpty(queryResult)) {
			return Collections.emptyMap();
		}
		Map<K, Map<K2, V>> result = new HashMap<>();
		for (Object[] row : queryResult) {
			K key = (K) row[0];
			K2 key2 = (K2) row[1];
			V value = (V) row[2];
			Map<K2, V> innerMap = result.get(key);
			if (innerMap == null) {
				innerMap = new HashMap<>();
				result.put(key, innerMap);
			}
			innerMap.put(key2, value);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public static <K, V> Map<K, Collection<V>> convertToSetMap(Collection<Object[]> queryResult) {
		if (CollectionUtils.isEmpty(queryResult)) {
			return Collections.emptyMap();
		}
		Map<K, Collection<V>> result = new HashMap<>();
		for (Object[] row : queryResult) {
			K key = (K) row[0];
			V value = (V) row[1];
			result.computeIfAbsent(key, k -> new HashSet<>()).add(value);
		}
		return result;
	}

	private static List<String> parseSelect(String selectString) {
		if (StringUtils.isEmpty(selectString)) {
			return Collections.emptyList();
		}

		List<String> resultList = new ArrayList<>();
		String[] parts = selectString.replaceAll("(?i)select", "").replaceAll("(?i)distinct", "").replaceAll("(?i)as DECIMAL\\(", "").split(", ");
		for (String part : parts) {
			String[] subParts = part.split("(?i) as ");
			String result = null;
			if (subParts.length == 1) {
				String path = subParts[0].trim();
				String[] pathParts = path.split("\\.");
				result = pathParts[pathParts.length - 1];
			} else {
				result = subParts[1].trim();
			}
			resultList.add(result);
		}

		return resultList;
	}

	public static void setPaging(QueryRequestDTO queryRequest, Query query) {
		if (queryRequest != null) {
			if (queryRequest.getPageNumber() > 0 && queryRequest.getRowsPerPage() > 0) {
				query.setParameter("offset", (queryRequest.getPageNumber() - 1) * queryRequest.getRowsPerPage());
				query.setParameter("limit", queryRequest.getRowsPerPage());
			}
			if (queryRequest.getOffset() >= 0 && queryRequest.getLimit() > 0) {
				query.setParameter("offset", queryRequest.getOffset());
				query.setParameter("limit", queryRequest.getLimit());
			}
		}
	}

	/*
	 * This method converts the list of values1 and list of values2 in correspondent query parameters, defined in correspondent sql query part, returned by getOrQueryParams() or
	 * getNullableOrQueryParams()
	 * Warning!
	 * Please, make sure that for any index i: either values1.get(i) or values2.get(i) is not empty
	 * Otherwise, you will get SQL compilation exception!
	 * Warning!
	 * Please, make sure that the sizes of values1 and values2 are the same. Otherwise, ArrayIndexOutOfBoundsException will be thrown
	 */
	public static void setOrQueryParamValues(String prefix1, String prefix2, List<String> values1, List<String> values2, Query query) throws ArrayIndexOutOfBoundsException {
		if (CollectionUtils.isEmpty(values1) || CollectionUtils.isEmpty(values2)) {
			return;
		}
		IntStream.range(0, values1.size()).forEach(i -> {
			if (StringUtils.isNotBlank(values1.get(i))) {
				query.setParameter(prefix1 + (i + 1), values1.get(i));
			}
			if (StringUtils.isNotBlank(values2.get(i))) {
				query.setParameter(prefix2 + (i + 1), values2.get(i));
			}
		});
	}

	public static void setOrQueryParamValues(List<String> values1, List<String> values2, Query query) throws ArrayIndexOutOfBoundsException {
		setOrQueryParamValues("m", "s", values1, values2, query);
	}

	/*
	 * This method converts the list of values1 and list of values2 in correspondent OR query
	 * Warning!
	 * Please, make sure that for any index i: either values1.get(i) or values2.get(i) is not empty
	 * Otherwise, you will get SQL compilation exception!
	 * Warning!
	 * Please, make sure that the sizes of values1 and values2 are the same. Otherwise, ArrayIndexOutOfBoundsException will be thrown
	 */
	public static String getOrQueryParams(String parameter1, String parameter2, String prefix1, String prefix2, String symbol, int size) {
		return "(" + IntStream.rangeClosed(1, size).mapToObj(i -> parameter1 + " = " + symbol + prefix1 + i + " AND " + parameter2 + " = " + symbol + prefix2 + i).collect(Collectors.joining(" OR "))
				+ ")";
	}

	public static String getOrJpaQueryParams(String parameter1, String parameter2, int size) {
		return getOrQueryParams(parameter1, parameter2, "m", "s", ":", size);
	}

	public static String getOrNativeQueryParams(String parameter1, String parameter2, int size) {
		return getOrQueryParams(parameter1, parameter2, "m", "s", "?", size);
	}

	/*
	 * This method converts the list of values1 and list of values2 in correspondent OR query
	 * Warning!
	 * Please, make sure that for any index i: either values1.get(i) or values2.get(i) is not empty
	 * Otherwise, you will get SQL compilation exception!
	 * Warning!
	 * Please, make sure that the sizes of values1 and values2 are the same. Otherwise, ArrayIndexOutOfBoundsException will be thrown
	 */
	public static String getNullableOrQueryParams(String parameter1, String parameter2, String prefix1, String prefix2, String symbol, List<String> values1, List<String> values2)
			throws ArrayIndexOutOfBoundsException {
		return "(" + IntStream.rangeClosed(1, values1.size()).mapToObj(i -> {
			String c1 = StringUtils.isNotBlank(values1.get(i - 1)) ? (parameter1 + " = " + symbol + prefix1 + i) : "";
			String c2 = StringUtils.isNotBlank(values2.get(i - 1)) ? (parameter2 + " = " + symbol + prefix2 + i) : "";
			if (StringUtils.isNotBlank(c1) && StringUtils.isNotBlank(c2)) {
				return c1 + " AND " + c2;
			} else {
				return c1 + c2;
			}
		}).collect(Collectors.joining(" OR ")) + ")";
	}

}
