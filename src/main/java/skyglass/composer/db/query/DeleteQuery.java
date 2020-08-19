package skyglass.composer.db.query;

import skyglass.composer.db.DBConnector;

public class DeleteQuery extends Query {

	private DeleteTable<?> table;

	public DeleteTable<?> from(String tableName) {
		this.table = new DeleteTable<>(this, tableName);

		return this.table;
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		if (this.table == null) {
			throw new IllegalStateException("From was not called so far");
		}

		StringBuilder strBuilder = new StringBuilder("DELETE ");

		strBuilder.append(this.table.build(dbType));
		strBuilder.append(";");

		return strBuilder.toString();
	}
}
