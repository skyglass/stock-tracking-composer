package skyglass.composer.db.query;

import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class DeleteTable<E extends DeleteTable<E>> extends UpsertTable<E> {
	public DeleteTable(Query query, String tableName) {
		this(query, null, tableName);
	}

	public DeleteTable(Query query, String alias, String tableName) {
		this(query, Pair.of(alias, tableName));
	}

	@SafeVarargs
	public DeleteTable(Query query, Pair<String, String>... aliasAndTableNames) {
		super(query, aliasAndTableNames);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E addTableNames(Pair<String, String>... aliasAndTableNames) {
		return super.addTableNames(aliasAndTableNames);
	}

	@Override
	public E addTableName(String alias, String tableName) {
		return super.addTableName(alias, tableName);
	}

	@Override
	public E addTableName(String tableName) {
		return super.addTableName(tableName);
	}

	@SuppressWarnings("unchecked")
	@Override
	public E where(WhereConditions conditions) {
		return super.where(conditions);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		StringBuilder strBuilder = new StringBuilder();

		strBuilder.append("FROM ");
		strBuilder.append(buildTableName(dbType));
		strBuilder.append(" ");

		if (this.whereConditions != null) {
			strBuilder.append("WHERE ");
			strBuilder.append(this.whereConditions.build(dbType));
			strBuilder.append(" ");
		}

		return strBuilder.toString().trim();
	}
}
