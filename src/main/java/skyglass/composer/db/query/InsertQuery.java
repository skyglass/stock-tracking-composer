package skyglass.composer.db.query;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;

import skyglass.composer.db.DBConnector;

public class InsertQuery extends Query {

	private final InsertTable<?> table;

	private String[] fields;

	public InsertQuery(String tableName, String... fields) {
		if (fields == null) {
			fields = new String[0];
		}

		this.table = new InsertTable<>(this, tableName);
		this.fields = fields;
	}

	public InsertQuery insert(String... fields) {
		if (fields != null && fields.length > 0) {
			this.fields = ArrayUtils.addAll(this.fields, fields);
		}

		return this;
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		if (fields == null || fields.length == 0) {
			throw new IllegalStateException("Fields cannot be empty or null");
		}

		StringBuilder strBuilder = new StringBuilder("INSERT INTO ");

		strBuilder.append(this.table.buildTableName(dbType));
		strBuilder.append(" (");
		strBuilder.append(Arrays.asList(this.fields).stream().map(field -> DBConnector.getColumnNamesAsString(dbType, field)).collect(Collectors.joining(", ")));
		strBuilder.append(") VALUES (");
		strBuilder.append(Arrays.asList(this.fields).stream().map(field -> "?").collect(Collectors.joining(", ")));
		strBuilder.append(") ");

		strBuilder.append(this.table.build(dbType));
		strBuilder.append(";");

		return strBuilder.toString();
	}
}
