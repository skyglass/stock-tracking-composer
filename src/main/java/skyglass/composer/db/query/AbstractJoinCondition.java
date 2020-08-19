package skyglass.composer.db.query;

public abstract class AbstractJoinCondition extends AbstractBuilder {
	public static enum Comparator {
		EQUALS("=", "!="), LESS_THAN("<", ">="), LESS_THAN_OR_EQUAL("<=", ">"), GREATER_THAN(">", "<="), GREATER_THAN_OR_EQUAL(">=", "<"), NULL("IS NULL", "IS NOT NULL");

		private final String asString;

		private final String invertedAsString;

		private Comparator(String asString, String invertedAsString) {
			this.asString = asString;
			this.invertedAsString = invertedAsString;
		}

		public String getAsString() {
			return asString;
		}

		public String getInvertedAsString() {
			return invertedAsString;
		}
	}

	public static enum Operator {
		AND, OR;
	}
}
