package skyglass.composer.query.request;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class ColumnIdHelper {

	public static String getCustomPivotValuesSql(ColumnId columnId, String sql) {

		return sql;
	}

	public static String getPeriodWhereSql(String columnName, Date from, Date to) {
		String result = getPeriodSql(columnName, ">=", from, to);
		if (StringUtils.isNotBlank(result)) {
			return "WHERE " + result + " ";
		}
		return "";
	}

	public static String getPeriodSql(String columnName, Date from, Date to) {
		return getPeriodSql(columnName, ">=", from, to);
	}

	public static String getPeriodSql(String columnName, String operator, Date from, Date to) {
		if (from != null && to != null) {
			return columnName + " >= " + from + " AND " + columnName + " <= " + to;
		}
		if (from != null) {
			return columnName + " " + operator + " " + from;
		} else {
			return "";
		}
	}

}
