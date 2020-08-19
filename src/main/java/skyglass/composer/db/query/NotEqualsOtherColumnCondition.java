package skyglass.composer.db.query;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class NotEqualsOtherColumnCondition extends EqualsCondition {
	private final String rightAlias;

	private final String rightField;

	NotEqualsOtherColumnCondition(String leftField, String rightField) {
		this(null, leftField, null, rightField);
	}

	NotEqualsOtherColumnCondition(String leftAlias, String leftField, String rightAlias, String rightField) {
		super(leftAlias, leftField, false, 0);

		this.rightAlias = rightAlias;
		this.rightField = rightField;
	}

	public static NotEqualsOtherColumnCondition create(String field) {
		throw new UnsupportedOperationException("Not supported in " + NotEqualsOtherColumnCondition.class.getSimpleName());
	}

	public static NotEqualsOtherColumnCondition create(String leftField, String rightField) {
		return create(null, leftField, null, rightField);
	}

	public static NotEqualsOtherColumnCondition create(String leftAlias, String leftField, String rightAlias, String rightField) {
		return new NotEqualsOtherColumnCondition(leftAlias, leftField, rightAlias, rightField);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		return build(dbType, null);
	}

	@Override
	String build(DBConnector.DatabaseType dbType, Pair<String, String> fromTableName) {
		if (DBConnector.DatabaseType.H2.equals(dbType) && fromTableName != null) {
			String fromAlias = fromTableName.getKey();
			if (!((StringUtils.isNotBlank(this.alias) && this.alias.equals(fromAlias)) || (StringUtils.isNotBlank(this.rightAlias) && this.rightAlias.equals(fromAlias))
					|| (StringUtils.isBlank(this.alias) && StringUtils.isBlank(this.rightAlias) && StringUtils.isBlank(fromAlias)))) {
				return null;
			}
		}

		return (StringUtils.isNotBlank(this.alias) ? this.alias + "." : "") + DBConnector.getColumnNamesAsString(dbType, this.field) + " "
				+ (this.not ? comparator.getInvertedAsString() : comparator.getAsString()) + " " + (StringUtils.isNotBlank(this.rightAlias) ? this.rightAlias + "." : "")
				+ DBConnector.getColumnNamesAsString(dbType, this.rightField);
	}

	@Override
	String buildForUpdateSet(DBConnector.DatabaseType dbType, List<Pair<String, String>> fromTableNames, WhereConditions whereConditions) {
		if (DBConnector.DatabaseType.H2.equals(dbType)) {
			if (CollectionUtils.isEmpty(fromTableNames)) {
				throw new IllegalArgumentException("The fromTableNames argument cannot be null");
			}

			Pair<String, String> aliasAndTableName = null;
			for (Pair<String, String> fromTableName : fromTableNames) {
				if (Objects.equals(this.rightAlias, fromTableName.getKey())) {
					aliasAndTableName = fromTableName;
					break;
				}
			}

			if (aliasAndTableName == null) {
				throw new IllegalArgumentException("Could not find matching table for condition " + this.build(dbType) + " in given tables " + fromTableNames.stream()
						.map(fromTableName -> (StringUtils.isBlank(fromTableName.getKey()) ? "" : fromTableName.getKey() + ".") + fromTableName.getValue()).collect(Collectors.joining(", ")));
			}

			String fromAlias = aliasAndTableName.getKey();
			String tableName = aliasAndTableName.getValue();

			if (((StringUtils.isNotBlank(this.alias) && this.alias.equals(fromAlias)) || (StringUtils.isNotBlank(this.rightAlias) && this.rightAlias.equals(fromAlias))
					|| (StringUtils.isBlank(this.alias) && StringUtils.isBlank(this.rightAlias) && StringUtils.isBlank(fromAlias)))) {
				return (StringUtils.isNotBlank(this.alias) ? this.alias + "." : "") + DBConnector.getColumnNamesAsString(dbType, this.field) + " "
						+ (this.not ? comparator.getInvertedAsString() : comparator.getAsString()) + " (SELECT "
						+ (StringUtils.isNotBlank(this.rightAlias) ? this.rightAlias + "." : "")
						+ DBConnector.getColumnNamesAsString(dbType, this.rightField) + " FROM " + DBConnector.getTableName(dbType, tableName)
						+ (StringUtils.isNotBlank(this.rightAlias) ? " " + this.rightAlias : "") + ((whereConditions != null) ? " WHERE " + whereConditions.build(dbType, aliasAndTableName) : "")
						+ ")";
			}
		}

		return build(dbType);
	}
}
