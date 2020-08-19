package skyglass.composer.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import skyglass.composer.db.DBConnector;

public class JoinConditions extends AbstractJoinCondition {
	private final List<AbstractJoinCondition> conditions = new ArrayList<>();

	private Operator operator;

	JoinConditions(AbstractJoinCondition condition) {
		if (condition == null) {
			throw new IllegalArgumentException("condition cannot be null");
		}

		this.conditions.add(condition);
	}

	@Override
	void setQuery(Query query) {
		super.setQuery(query);

		for (AbstractJoinCondition condition : this.conditions) {
			condition.setQuery(this.query);
		}
	}

	public JoinConditions and(AbstractJoinCondition... conditions) {
		if (conditions == null || conditions.length == 0) {
			return this;
		}

		if (this.operator != null && !Operator.AND.equals(this.operator)) {
			throw new UnsupportedOperationException("Cannot combine AND with OR in one expression!");
		}

		this.operator = Operator.AND;

		List<AbstractJoinCondition> conditionList = Arrays.asList(conditions);
		conditionList.forEach(condition -> condition.setQuery(this.query));
		this.conditions.addAll(conditionList);

		return this;
	}

	public JoinConditions or(AbstractJoinCondition... conditions) {
		if (conditions == null || conditions.length == 0) {
			return this;
		}

		if (this.operator != null && !Operator.OR.equals(this.operator)) {
			throw new UnsupportedOperationException("Cannot combine AND with OR in one expression!");
		}

		this.operator = Operator.OR;

		List<AbstractJoinCondition> conditionList = Arrays.asList(conditions);
		conditionList.forEach(condition -> condition.setQuery(this.query));
		this.conditions.addAll(conditionList);

		return this;
	}

	public static JoinConditions equals(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(JoinCondition.equals(leftAlias, leftField, rightAlias, rightField));
	}

	public static JoinConditions notEquals(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(JoinCondition.notEquals(leftAlias, leftField, rightAlias, rightField));
	}

	public static JoinConditions isNull(String leftAlias, String leftField) {
		return condition(JoinCondition.isNull(leftAlias, leftField));
	}

	public static JoinConditions isNotNull(String leftAlias, String leftField) {
		return condition(JoinCondition.isNotNull(leftAlias, leftField));
	}

	public static JoinConditions lessThan(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(JoinCondition.lessThan(leftAlias, leftField, rightAlias, rightField));
	}

	public static JoinConditions lessThanOrEqual(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(JoinCondition.lessThanOrEqual(leftAlias, leftField, rightAlias, rightField));
	}

	public static JoinConditions greaterThan(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(JoinCondition.greaterThan(leftAlias, leftField, rightAlias, rightField));
	}

	public static JoinConditions greaterThanOrEqual(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(JoinCondition.greaterThanOrEqual(leftAlias, leftField, rightAlias, rightField));
	}

	private static JoinConditions condition(JoinCondition condition) {
		return new JoinConditions(condition);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		return this.conditions.stream().map(condition -> {
			boolean surroundingBrakets = false;

			if (condition instanceof JoinConditions) {
				JoinConditions c = (JoinConditions) condition;
				if (c.conditions.size() > 1) {
					surroundingBrakets = true;
				}
			}

			String ret = condition.build(dbType);
			if (surroundingBrakets) {
				ret = "(" + ret + ")";
			}

			return ret;
		}).collect(Collectors.joining(" " + this.operator + " "));
	}
}
