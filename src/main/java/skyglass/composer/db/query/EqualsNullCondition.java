package skyglass.composer.db.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class EqualsNullCondition extends EqualsCondition {
	EqualsNullCondition(String leftAlias, String leftField) {
		super(leftAlias, leftField, true, 0);
	}

	public static EqualsNullCondition create(String leftField) {
		return EqualsNullCondition.create(null, leftField);
	}

	public static EqualsNullCondition create(String leftAlias, String leftField) {
		return new EqualsNullCondition(leftAlias, leftField);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		return build(dbType, null);
	}

	@Override
	String build(DBConnector.DatabaseType dbType, Pair<String, String> fromTableName) {
		if (DBConnector.DatabaseType.H2.equals(dbType) && fromTableName != null) {
			String fromAlias = fromTableName.getKey();
			if (!((StringUtils.isNotBlank(this.alias) && this.alias.equals(fromAlias)) || (StringUtils.isBlank(this.alias) && StringUtils.isBlank(fromAlias)))) {
				return null;
			}
		}

		return (StringUtils.isNotBlank(this.alias) ? this.alias + "." : "") + DBConnector.getColumnNamesAsString(dbType, this.field) + " "
				+ (this.not ? comparator.getInvertedAsString() : comparator.getAsString()) + " NULL";
	}
}
