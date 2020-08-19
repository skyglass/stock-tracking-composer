package skyglass.composer.db.query;

import skyglass.composer.db.DBConnector;

public class QueryHelper {
	private final DBConnector.DatabaseType dbType;

	public QueryHelper(DBConnector.DatabaseType dbType) {
		this.dbType = dbType;
	}

	public String where(WhereConditions condition) {
		if (condition == null) {
			return "";
		}

		return condition.build(dbType);
	}
}
