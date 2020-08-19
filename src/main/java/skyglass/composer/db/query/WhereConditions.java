package skyglass.composer.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class WhereConditions extends AbstractWhereCondition {
	private final List<AbstractWhereCondition> conditions = new ArrayList<>();

	private Operator operator;

	WhereConditions(AbstractBaseWhereCondition condition) {
		if (condition == null) {
			throw new IllegalArgumentException("condition cannot be null");
		}

		this.conditions.add(condition);
	}

	List<AbstractWhereCondition> getConditions() {
		return this.conditions;
	}

	@Override
	void setQuery(Query query) {
		super.setQuery(query);

		for (AbstractWhereCondition condition : this.conditions) {
			condition.setQuery(this.query);
		}
	}

	public WhereConditions and(AbstractWhereCondition... conditions) {
		if (conditions == null || conditions.length == 0) {
			return this;
		}

		if (this.operator != null && !Operator.AND.equals(this.operator)) {
			throw new UnsupportedOperationException("Cannot combine AND with OR in one expression!");
		}

		this.operator = Operator.AND;

		List<AbstractWhereCondition> conditionList = Arrays.asList(conditions);
		conditionList.forEach(condition -> condition.setQuery(this.query));
		this.conditions.addAll(conditionList);

		return this;
	}

	public WhereConditions or(AbstractWhereCondition... conditions) {
		if (conditions == null || conditions.length == 0) {
			return this;
		}

		if (this.operator != null && !Operator.OR.equals(this.operator)) {
			throw new UnsupportedOperationException("Cannot combine AND with OR in one expression!");
		}

		this.operator = Operator.OR;

		List<AbstractWhereCondition> conditionList = Arrays.asList(conditions);
		conditionList.forEach(condition -> condition.setQuery(this.query));
		this.conditions.addAll(conditionList);

		return this;
	}

	public static WhereConditions equals(String field) {
		return equals(null, field);
	}

	public static WhereConditions equals(String alias, String field) {
		return condition(WhereCondition.equals(alias, field));
	}

	public static WhereConditions notEquals(String field) {
		return notEquals(null, field);
	}

	public static WhereConditions notEquals(String alias, String field) {
		return condition(WhereCondition.notEquals(alias, field));
	}

	public static WhereConditions equalsOtherColumn(String leftField, String rightField) {
		return equalsOtherColumn(null, leftField, null, rightField);
	}

	public static WhereConditions equalsOtherColumn(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(EqualsOtherColumnCondition.create(leftAlias, leftField, rightAlias, rightField));
	}

	public static WhereConditions notEqualsOtherColumn(String leftField, String rightField) {
		return notEqualsOtherColumn(null, leftField, null, rightField);
	}

	public static WhereConditions notEqualsOtherColumn(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(NotEqualsOtherColumnCondition.create(leftAlias, leftField, rightAlias, rightField));
	}

	public static WhereConditions in(String field, int expectedValues) {
		return in(null, field, expectedValues);
	}

	public static WhereConditions in(String alias, String field, int expectedValues) {
		return condition(WhereCondition.in(alias, field, expectedValues));
	}

	public static WhereConditions notIn(String field, int expectedValues) {
		return notIn(null, field, expectedValues);
	}

	public static WhereConditions notIn(String alias, String field, int expectedValues) {
		return condition(WhereCondition.notIn(alias, field, expectedValues));
	}

	public static WhereConditions like(String field) {
		return like(null, field);
	}

	public static WhereConditions like(String alias, String field) {
		return condition(WhereCondition.like(alias, field));
	}

	public static WhereConditions notLike(String field) {
		return notLike(null, field);
	}

	public static WhereConditions notLike(String alias, String field) {
		return condition(WhereCondition.notLike(alias, field));
	}

	public static WhereConditions isNull(String field) {
		return isNull(null, field);
	}

	public static WhereConditions isNull(String alias, String field) {
		return condition(WhereCondition.isNull(alias, field));
	}

	public static WhereConditions isNotNull(String field) {
		return isNotNull(null, field);
	}

	public static WhereConditions isNotNull(String alias, String field) {
		return condition(WhereCondition.isNotNull(alias, field));
	}

	public static WhereConditions lessThan(String field) {
		return lessThan(null, field);
	}

	public static WhereConditions lessThan(String alias, String field) {
		return condition(WhereCondition.lessThan(alias, field));
	}

	public static WhereConditions lessThanOrEqual(String field) {
		return lessThanOrEqual(null, field);
	}

	public static WhereConditions lessThanOrEqual(String alias, String field) {
		return condition(WhereCondition.lessThanOrEqual(alias, field));
	}

	public static WhereConditions greaterThan(String field) {
		return greaterThan(null, field);
	}

	public static WhereConditions greaterThan(String alias, String field) {
		return condition(WhereCondition.greaterThan(alias, field));
	}

	public static WhereConditions greaterThanOrEqual(String field) {
		return greaterThanOrEqual(null, field);
	}

	public static WhereConditions greaterThanOrEqual(String alias, String field) {
		return condition(WhereCondition.greaterThanOrEqual(alias, field));
	}

	private static WhereConditions condition(AbstractBaseWhereCondition condition) {
		return new WhereConditions(condition);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		return build(dbType, null);
	}

	@Override
	String build(DBConnector.DatabaseType dbType, Pair<String, String> fromTableName) {
		return this.conditions.stream().map(condition -> {
			boolean surroundingBrakets = false;

			if (condition instanceof WhereConditions) {
				WhereConditions c = (WhereConditions) condition;
				if (c.conditions.size() > 1) {
					surroundingBrakets = true;
				}
			}

			String ret = condition.build(dbType, fromTableName);
			if (StringUtils.isNotBlank(ret) && surroundingBrakets) {
				ret = "(" + ret + ")";
			}

			return ret;
		}).filter(condition -> StringUtils.isNotBlank(condition)).collect(Collectors.joining(" " + this.operator + " "));
	}
}
