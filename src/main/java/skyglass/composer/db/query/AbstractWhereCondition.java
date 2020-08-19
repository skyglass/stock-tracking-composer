package skyglass.composer.db.query;

import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public abstract class AbstractWhereCondition extends AbstractBuilder {
	public static enum Comparator {
		EQUALS("=", "!="), IN("IN", "NOT IN"), LESS_THAN("<", ">="), LESS_THAN_OR_EQUAL("<=", ">"), GREATER_THAN(">", "<="), GREATER_THAN_OR_EQUAL(">=", "<"), NULL("IS NULL", "IS NOT NULL"), LIKE(
				"LIKE",
				"NOT LIKE");

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

	abstract String build(DBConnector.DatabaseType dbType, Pair<String, String> fromTableName);
}
