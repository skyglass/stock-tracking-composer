package skyglass.composer.query.request;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.query.builder.CustomQueryBuilder;
import skyglass.composer.stock.entity.model.EntityUtil;

public class ColumnIdHelper {

	@SuppressWarnings("unchecked")
	public static List<String> getPivotValuesResult(Query query) {
		return (List<String>) EntityUtil.getListResultSafely(query).stream().filter(e -> e != null).map(e -> e.toString()).collect(Collectors.toList());
	}

	public static String getCustomPivotValuesSql(CustomQueryBuilder queryBuilder, ColumnId columnId, String sql) {
		addCustomColumn(queryBuilder, columnId, sql, ColumnId.customColumn1);
		addCustomColumn(queryBuilder, columnId, sql, ColumnId.customColumn2);
		addCustomColumn(queryBuilder, columnId, sql, ColumnId.customColumn3);
		addCustomColumn(queryBuilder, columnId, sql, ColumnId.customColumn4);
		addCustomColumn(queryBuilder, columnId, sql, ColumnId.customColumn5);
		return sql;
	}

	private static void addCustomColumn(CustomQueryBuilder queryBuilder, ColumnId columnId, String sql, ColumnId targetColumnId) {
		if (columnId == targetColumnId) {
			if (sql.contains("WHERE")) {
				sql += " AND ";
			} else {
				sql += " WHERE ";
			}
			sql += "LOWER("
					+ columnId.getTableAlias()
					+ ".reference) = '"
					+ queryBuilder.getCustomColumnLowerCase(columnId)
					+ "'";
		}
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
			return columnName + " >= " + getTimestamp(from) + " AND " + columnName + " <= " + getTimestamp(to);
		}
		if (from != null) {
			return columnName + " " + operator + " " + getTimestamp(from);
		} else {
			return "";
		}
	}

	private static String getTimestamp(Date date) {
		return String.format("'%s'", new Timestamp(date.getTime()).toString());
	}

}
