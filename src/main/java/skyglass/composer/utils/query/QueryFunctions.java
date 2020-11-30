package skyglass.composer.utils.query;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.db.DatabaseType;

public class QueryFunctions {

	private static String coalesce(String[] fieldResolvers, boolean lower) {
		StringBuilder sb = new StringBuilder();
		sb.append("COALESCE(");
		int i = 0;
		for (String fieldResolver : fieldResolvers) {
			if (i > 0) {
				sb.append(",");
			}
			sb.append(lower ? lower(fieldResolver) : fieldResolver);
			i++;
		}
		sb.append(")");
		return sb.toString();
	}

	public static String lower(String fieldResolver) {
		return String.format("LOWER(TRIM(%s))", fieldResolver);
	}

	public static String coalesce(String... fieldResolvers) {
		return coalesce(fieldResolvers, false);
	}

	public static String lowerCoalesce(String... fieldResolvers) {
		return coalesce(fieldResolvers, true);
	}

	public static String and(String queryStr1, String queryStr2) {
		return StringUtils.isNotBlank(queryStr1) ? (queryStr1 + " AND " + queryStr2) : queryStr2;
	}

	public static <T extends Enum<T>> String ordinalToString(Enum<T>[] enumValues, String path) {
		return String.format(getEnumString(enumValues), path);
	}

	private static <T extends Enum<T>> String getEnumString(Enum<T>[] enumValues) {
		String result = "CASE %s";
		for (Enum<T> value : enumValues) {
			result += " WHEN " + value.ordinal() + " THEN '" + value.toString() + "'";
		}
		result += " END";
		return result;
	}

	public static <T extends Enum<T>> String stringToOrdinal(Enum<T>[] enumValues, String path) {
		return String.format(getEnumOrdinal(enumValues), path);
	}

	private static <T extends Enum<T>> String getEnumOrdinal(Enum<T>[] enumValues) {
		String result = "CASE %s";
		for (Enum<T> value : enumValues) {
			result += " WHEN '" + value.toString() + "' THEN " + value.ordinal();
		}
		result += " END";
		return result;
	}

	public static String secondsBetween(String startDate, String endDate, DatabaseType databaseType) {
		switch (databaseType) {
			case SAP_HANA:
				return String.format("SECONDS_BETWEEN(%s, %s)", endDate, startDate);
			case POSTGRE_SQL:
				return String.format("EXTRACT(EPOCH FROM (%s - %s))", startDate, endDate);
			case H2:
			default:
				return String.format("CAST(DATEDIFF('SECOND', %s, %s) AS BIGINT)", endDate, startDate);
		}
	}

	private static String utcToLocal(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case SAP_HANA:
				return String.format("ADD_SECONDS(%s, %s)", date, offsetSeconds);
			case POSTGRE_SQL:
				return String.format("(%s::TIMESTAMP + '%s SECONDS'::INTERVAL)::TIMESTAMP", date, offsetSeconds);
			case H2:
				return String.format("TIMESTAMPADD(SECOND, %s, %s)", offsetSeconds, date);
			default:
				return date;
		}
	}

	public static String year(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(YEAR FROM %s)::INTEGER", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return String.format("YEAR(%s)", utcToLocal(date, offsetSeconds, databaseType));
		}
	}

	public static String month(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(MONTH FROM %s)::INTEGER", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return String.format("MONTH(%s)", utcToLocal(date, offsetSeconds, databaseType));
		}
	}

	public static String week(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(WEEK FROM %s)::INTEGER", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return String.format("WEEK(%s)", utcToLocal(date, offsetSeconds, databaseType));
		}
	}

	public static String dayOfMonth(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(DAY FROM %s)::INTEGER", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return String.format("DAYOFMONTH(%s)", utcToLocal(date, offsetSeconds, databaseType));
		}
	}

	public static String decode(String field, String value, String expression, DatabaseType databaseType) {
		return String.format("CASE WHEN %s = %s THEN %s END", field, value, expression);
	}

	public static String distinct(String sql, DatabaseType databaseType) {
		switch (databaseType) {
			case SAP_HANA:
				return String.format("DISTINCT %s", sql);
			case POSTGRE_SQL:
			case H2:
			default:
				return String.format("DISTINCT(%s)", sql);
		}
	}

}
