package skyglass.composer.db.query;

import org.apache.commons.lang3.tuple.Pair;

public abstract class QueryFactory {
	public static SelectQuery select(String... fields) {
		return new SelectQuery(fields);
	}

	@SafeVarargs
	public static SelectQuery select(Pair<String, String>... fields) {
		return new SelectQuery(fields);
	}

	public static InsertQuery insert(String tableName, String... fields) {
		return new InsertQuery(tableName, fields);
	}

	public static UpdateQuery update(String tableName, EqualsCondition... conditions) {
		return update(null, tableName, conditions);
	}

	public static UpdateQuery update(String alias, String tableName, EqualsCondition... conditions) {
		return new UpdateQuery(alias, tableName, conditions);
	}

	public static UpsertQuery upsert(String tableName, String... fields) {
		return new UpsertQuery(tableName, fields);
	}

	public static DeleteQuery delete() {
		return new DeleteQuery();
	}
}
