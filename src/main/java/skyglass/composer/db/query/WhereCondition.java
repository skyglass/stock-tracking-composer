package skyglass.composer.db.query;

public class WhereCondition extends AbstractBaseWhereCondition {
	WhereCondition(String field, Comparator comparator, boolean usePositivCase) {
		this(null, field, comparator, usePositivCase);
	}

	WhereCondition(String field, Comparator comparator, boolean usePositivCase, int expectedValues) {
		this(null, field, comparator, usePositivCase, expectedValues);
	}

	WhereCondition(String alias, String field, Comparator comparator, boolean usePositivCase) {
		this(alias, field, comparator, usePositivCase, 1);
	}

	WhereCondition(String alias, String field, Comparator comparator, boolean usePositivCase, int expectedValues) {
		super(alias, field, comparator, usePositivCase, expectedValues);
	}

	public static AbstractBaseWhereCondition equals(String field) {
		return equals(null, field);
	}

	public static AbstractBaseWhereCondition equals(String alias, String field) {
		return create(alias, field, Comparator.EQUALS, true);
	}

	public static AbstractBaseWhereCondition notEquals(String field) {
		return notEquals(null, field);
	}

	public static AbstractBaseWhereCondition notEquals(String alias, String field) {
		return create(alias, field, Comparator.EQUALS, false);
	}

	public static AbstractBaseWhereCondition in(String field, int expectedValues) {
		return in(null, field, expectedValues);
	}

	public static AbstractBaseWhereCondition in(String alias, String field, int expectedValues) {
		return create(alias, field, Comparator.IN, true, expectedValues);
	}

	public static AbstractBaseWhereCondition notIn(String field, int expectedValues) {
		return notIn(null, field, expectedValues);
	}

	public static AbstractBaseWhereCondition notIn(String alias, String field, int expectedValues) {
		return create(alias, field, Comparator.IN, false, expectedValues);
	}

	public static AbstractBaseWhereCondition like(String field) {
		return like(null, field);
	}

	public static AbstractBaseWhereCondition like(String alias, String field) {
		return create(alias, field, Comparator.LIKE, true);
	}

	public static AbstractBaseWhereCondition notLike(String field) {
		return notLike(null, field);
	}

	public static AbstractBaseWhereCondition notLike(String alias, String field) {
		return create(alias, field, Comparator.LIKE, false);
	}

	public static AbstractBaseWhereCondition isNull(String field) {
		return isNull(null, field);
	}

	public static AbstractBaseWhereCondition isNull(String alias, String field) {
		return create(alias, field, Comparator.NULL, true, 0);
	}

	public static AbstractBaseWhereCondition isNotNull(String field) {
		return isNotNull(null, field);
	}

	public static AbstractBaseWhereCondition isNotNull(String alias, String field) {
		return create(alias, field, Comparator.NULL, false, 0);
	}

	public static AbstractBaseWhereCondition lessThan(String field) {
		return lessThan(null, field);
	}

	public static AbstractBaseWhereCondition lessThan(String alias, String field) {
		return create(alias, field, Comparator.LESS_THAN, true);
	}

	public static AbstractBaseWhereCondition lessThanOrEqual(String field) {
		return lessThanOrEqual(null, field);
	}

	public static AbstractBaseWhereCondition lessThanOrEqual(String alias, String field) {
		return create(alias, field, Comparator.LESS_THAN_OR_EQUAL, true);
	}

	public static AbstractBaseWhereCondition greaterThan(String field) {
		return greaterThan(null, field);
	}

	public static AbstractBaseWhereCondition greaterThan(String alias, String field) {
		return create(alias, field, Comparator.GREATER_THAN, true);
	}

	public static AbstractBaseWhereCondition greaterThanOrEqual(String field) {
		return greaterThanOrEqual(null, field);
	}

	public static AbstractBaseWhereCondition greaterThanOrEqual(String alias, String field) {
		return create(alias, field, Comparator.GREATER_THAN_OR_EQUAL, true);
	}

	private static AbstractBaseWhereCondition create(String alias, String field, Comparator comparator, boolean usePositiveCase) {
		return create(alias, field, comparator, usePositiveCase, 1);
	}

	private static AbstractBaseWhereCondition create(String alias, String field, Comparator comparator, boolean usePositiveCase, int expectedValue) {
		return new WhereCondition(alias, field, comparator, usePositiveCase, expectedValue);
	}
}
