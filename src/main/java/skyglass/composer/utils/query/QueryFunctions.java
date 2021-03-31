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
				return String.format("ISO_WEEK(%s)", utcToLocal(date, offsetSeconds, databaseType));
			case SAP_HANA:
			default:
				return String.format("ISOWEEK(%s)", utcToLocal(date, offsetSeconds, databaseType));
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

	public static String dayOfYear(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(DOY FROM %s)::INTEGER", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return String.format("DAYOFYEAR(%s)", utcToLocal(date, offsetSeconds, databaseType));
		}
	}

	public static String yearString(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(YEAR FROM %s)::VARCHAR", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return year(date, offsetSeconds, databaseType);
		}
	}

	public static String monthString(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(MONTH FROM %s)::VARCHAR", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return month(date, offsetSeconds, databaseType);
		}
	}

	public static String weekString(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(WEEK FROM %s)::VARCHAR", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return week(date, offsetSeconds, databaseType);
		}
	}

	public static String dayOfMonthString(String date, String offsetSeconds, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
				return String.format("EXTRACT(DAY FROM %s)::VARCHAR", utcToLocal(date, offsetSeconds, databaseType));
			case H2:
			case SAP_HANA:
			default:
				return dayOfMonth(date, offsetSeconds, databaseType);
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

	public static int getWeekResult(Object sqlResult, DatabaseType databaseType) {
		switch (databaseType) {
			case POSTGRE_SQL:
			case H2:
				return (int) sqlResult;
			case SAP_HANA:
			default:
				String stringResult = sqlResult.toString();
				int startIndex = stringResult.indexOf("-W") + 2;
				return (int) Integer.parseInt(stringResult.substring(startIndex));

		}
	}

	public static String stringToNumeric(String expression, DatabaseType databaseType) {
		String preparedExpression = String.format("REPLACE(REPLACE(%s, ',', '.'), ' ', '')", expression);
		return "CASE WHEN "
				+ expression
				+ " IS NULL THEN 0"
				+ " WHEN " + regexpReplace(preparedExpression, ".*?((-?\\d+\\.?\\d*)[^0-9]*)?$", "\\2", databaseType)
				+ " = '' THEN 0 ELSE "
				+ String.format("CAST(%s AS DECIMAL(11,4))", regexpReplace(preparedExpression, ".*?((-?\\d+\\.?\\d*)[^0-9]*)?$", "\\2", databaseType))
				+ " END";
	}

	public static String regexpReplace(String expression, String pattern, String replacementString, DatabaseType databaseType) {
		switch (databaseType) {
			case H2:
			case POSTGRE_SQL:
				return String.format("REGEXP_REPLACE(%s, '%s', '%s')", expression, pattern, replacementString);
			case SAP_HANA:
			default:
				return String.format("REPLACE_REGEXPR('%s' IN %s WITH '%s')", pattern, expression, replacementString);
		}
	}

}
