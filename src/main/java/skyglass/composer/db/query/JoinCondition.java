package skyglass.composer.db.query;

import org.apache.commons.lang3.StringUtils;

import skyglass.composer.db.DBConnector;

public class JoinCondition extends AbstractJoinCondition {
	protected final String leftAlias;

	protected final String leftField;

	protected final String rightAlias;

	protected final String rightField;

	protected final Comparator comparator;

	protected final boolean not;

	JoinCondition(String leftAlias, String leftField, Comparator comparator, String rightAlias, String rightField, boolean usePositivCase) {
		this.leftAlias = leftAlias;
		this.leftField = leftField;
		this.comparator = comparator;
		this.rightAlias = rightAlias;
		this.rightField = rightField;
		this.not = !usePositivCase;
	}

	public String getLeftField() {
		return leftField;
	}

	public Comparator getComparator() {
		return comparator;
	}

	public boolean isNot() {
		return not;
	}

	@Override
	void setQuery(Query query) {
		this.query = query;
	}

	public static JoinCondition equals(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(leftAlias, leftField, Comparator.EQUALS, rightAlias, rightField, true);
	}

	public static JoinCondition notEquals(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(leftAlias, leftField, Comparator.EQUALS, rightAlias, rightField, false);
	}

	public static JoinCondition isNull(String leftAlias, String leftField) {
		return condition(leftAlias, leftField, Comparator.NULL, null, null, true);
	}

	public static JoinCondition isNotNull(String leftAlias, String leftField) {
		return condition(leftAlias, leftField, Comparator.NULL, null, null, false);
	}

	public static JoinCondition lessThan(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(leftAlias, leftField, Comparator.LESS_THAN, rightAlias, rightField, true);
	}

	public static JoinCondition lessThanOrEqual(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(leftAlias, leftField, Comparator.LESS_THAN_OR_EQUAL, rightAlias, rightField, true);
	}

	public static JoinCondition greaterThan(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(leftAlias, leftField, Comparator.GREATER_THAN, rightAlias, rightField, true);
	}

	public static JoinCondition greaterThanOrEqual(String leftAlias, String leftField, String rightAlias, String rightField) {
		return condition(leftAlias, leftField, Comparator.GREATER_THAN_OR_EQUAL, rightAlias, rightField, true);
	}

	private static JoinCondition condition(String leftAlias, String leftField, Comparator comparator, String rightAlias, String rightField, boolean usePositiveCase) {
		return new JoinCondition(leftAlias, leftField, comparator, rightAlias, rightField, usePositiveCase);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		return this.leftAlias + "." + DBConnector.getColumnNamesAsString(dbType, this.leftField) + " " + (this.not ? comparator.getInvertedAsString() : comparator.getAsString()) + " "
				+ (!StringUtils.isAllBlank(this.rightAlias, this.rightField) ? (this.rightAlias
						+ "." + DBConnector.getColumnNamesAsString(dbType, this.rightField)) : "");
	}
}
