package skyglass.composer.db.query;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public abstract class AbstractBaseWhereCondition extends AbstractWhereCondition {
	protected final String field;

	protected final Comparator comparator;

	protected final boolean not;

	protected final int expectedValues;

	protected String alias;

	AbstractBaseWhereCondition(String alias, String field, Comparator comparator, boolean usePositivCase, int expectedValues) {
		this.alias = alias;
		this.field = field;
		this.comparator = comparator;
		this.not = !usePositivCase;
		this.expectedValues = expectedValues;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	protected String expectedValues(int expectedValues) {
		StringBuilder strBuilder = new StringBuilder();

		for (int i = 0; i < expectedValues; i++) {
			if (i != 0) {
				strBuilder.append(",");
			}

			strBuilder.append(" ?");
		}

		if (expectedValues > 1 || Comparator.IN.equals(this.comparator)) {
			strBuilder.insert(1, "(");
			strBuilder.append(")");
		}

		return strBuilder.toString();
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
				+ (this.not ? this.comparator.getInvertedAsString() : this.comparator.getAsString()) + expectedValues(this.expectedValues);
	}
}
