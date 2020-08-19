package skyglass.composer.db.query;

import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class InsertTable<E extends InsertTable<E>> extends AbstractTable<E> {
	public InsertTable(Query query, String tableName) {
		this(query, Pair.of(null, tableName));
	}

	@SafeVarargs
	protected InsertTable(Query query, Pair<String, String>... aliasAndTableNames) {
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

	String buildTableName(DBConnector.DatabaseType dbType) {
		return DBConnector.getTableName(dbType, this.fromTableNames.get(0).getValue());
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		return "";
	}
}
