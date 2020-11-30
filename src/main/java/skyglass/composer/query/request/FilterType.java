package skyglass.composer.query.request;

public enum FilterType {

	equals("equals", "="), //
	notEqual("notEqual", "<>"), //
	lessThan("lessThan", "<"), //
	lessThanOrEqual("lessThanOrEqual", "<="), //
	greaterThan("greaterThan", ">"), //
	greaterThanOrEqual("greaterThanOrEqual", ">="), //
	inRange("inRange", null);

	private final String type;

	private final String operator;

	private FilterType(String type, String operator) {
		this.type = type;
		this.operator = operator;
	}

	public String getType() {
		return type;
	}

	public String getOperator() {
		return operator;
	}

}
