package skyglass.composer.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import skyglass.composer.db.DBConnector;

public class UpsertQuery extends Query {

	private final UpsertTable<?> table;

	private String[] fields;

	public UpsertQuery(String tableName, String... fields) {
		if (fields == null) {
			fields = new String[0];
		}

		this.table = new UpsertTable<>(this, tableName);
		this.fields = fields;
	}

	public UpsertQuery upsert(String... fields) {
		if (fields != null && fields.length > 0) {
			this.fields = ArrayUtils.addAll(this.fields, fields);
		}

		return this;
	}

	public InsertTable<?> where(WhereConditions conditions) {
		return (InsertTable<?>) this.table.where(conditions);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		if (fields == null || fields.length == 0) {
			throw new IllegalStateException("Fields cannot be empty or null");
		}

		StringBuilder strBuilder = new StringBuilder();

		switch (dbType) {
			case POSTGRE_SQL:
			case UNKNOWN: {
				strBuilder.append("INSERT INTO ");
				strBuilder.append(this.table.buildTableName(dbType));
				strBuilder.append(" (");
				strBuilder.append(Arrays.asList(this.fields).stream().map(field -> DBConnector.getColumnNamesAsString(dbType, field))
						.collect(Collectors.joining(", ")));
				strBuilder.append(") VALUES (");
				strBuilder.append(Arrays.asList(this.fields).stream().map(field -> "?").collect(Collectors.joining(", ")));
				strBuilder.append(") ON CONFLICT (uuid) DO UPDATE SET ");
				strBuilder.append(Arrays.asList(this.fields).stream()
						.map(field -> DBConnector.getColumnNamesAsString(dbType, field) + " = EXCLUDED." + DBConnector.getColumnNamesAsString(dbType, field))
						.collect(Collectors.joining(", ")));
				strBuilder.append(" ");
				strBuilder.append(this.table.build(dbType));
				strBuilder.append(";");

				break;
			}
			case H2: {
				strBuilder.append("MERGE INTO ");
				strBuilder.append(this.table.buildTableName(dbType));
				strBuilder.append(" (");
				strBuilder.append(Arrays.asList(this.fields).stream().map(field -> DBConnector.getColumnNamesAsString(dbType, field))
						.collect(Collectors.joining(", ")));
				strBuilder.append(") KEY (");
				strBuilder.append(getWhereFieldnames(this.table.whereConditions, dbType).stream().collect(Collectors.joining(", ")));
				strBuilder.append(") VALUES (");
				strBuilder.append(Arrays.asList(this.fields).stream().map(field -> "?").collect(Collectors.joining(", ")));
				strBuilder.append(");");

				break;
			}
		}

		return strBuilder.toString();
	}

	private List<String> getWhereFieldnames(WhereConditions whereConditons, DBConnector.DatabaseType dbType) {
		List<String> fieldnames = new ArrayList<>();

		List<AbstractWhereCondition> conditions = whereConditons.getConditions();
		for (AbstractWhereCondition condition : conditions) {
			if (condition instanceof AbstractBaseWhereCondition) {
				AbstractBaseWhereCondition baseCondition = (AbstractBaseWhereCondition) condition;
				String fieldname = "";
				if (StringUtils.isNotBlank(baseCondition.alias)) {
					fieldname = baseCondition.alias + ".";
				}

				fieldname += DBConnector.getColumnNamesAsString(dbType, baseCondition.field);

				fieldnames.add(fieldname);
			} else if (condition instanceof WhereConditions) {
				fieldnames.addAll(getWhereFieldnames((WhereConditions) condition, dbType));
			}
		}

		return fieldnames;
	}
}
