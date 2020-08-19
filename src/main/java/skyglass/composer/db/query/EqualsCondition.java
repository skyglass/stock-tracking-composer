package skyglass.composer.db.query;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class EqualsCondition extends AbstractBaseWhereCondition {
	EqualsCondition(String field) {
		this(null, field);
	}

	EqualsCondition(String alias, String field) {
		this(alias, field, true, 1);
	}

	protected EqualsCondition(String alias, String field, boolean usePositiveCase, int expectedValue) {
		super(alias, field, Comparator.EQUALS, usePositiveCase, expectedValue);
	}

	public static EqualsCondition create(String field) {
		return create(null, field);
	}

	public static EqualsCondition create(String alias, String field) {
		return new EqualsCondition(alias, field);
	}

	String buildForUpdateSet(DBConnector.DatabaseType dbType, List<Pair<String, String>> fromTableNames, WhereConditions whereConditions) {
		return build(dbType);
	}
}
