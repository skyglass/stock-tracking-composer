package skyglass.composer.db.query;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class SelectQuery extends Query {

	private final Set<Pair<String, String>> fields = new HashSet<>();

	private SelectTable<?> table;

	@SuppressWarnings("unchecked")
	public SelectQuery(String... fields) {
		this(fields == null || fields.length == 0 ? null : Arrays.asList(fields).stream().map(field -> Pair.of(null, field)).collect(Collectors.toList()).toArray(new Pair[0]));
	}

	@SuppressWarnings("unchecked")
	public SelectQuery(Pair<String, String>... fields) {
		if (fields != null && fields.length > 0) {
			for (Pair<String, String> field : fields) {
				String alias = field.getKey();
				String fieldName = field.getValue();

				if (StringUtils.isBlank(fieldName)) {
					throw new IllegalArgumentException("Field name cannot be empty or null");
				}

				this.fields.add(Pair.of(alias, fieldName));
			}
		}
	}

	@SuppressWarnings("unchecked")
	public SelectQuery select(Pair<String, String>... fields) {
		if (fields != null && fields.length > 0) {
			for (Pair<String, String> field : fields) {
				String alias = field.getKey();
				String fieldName = field.getValue();

				if (StringUtils.isBlank(fieldName)) {
					throw new IllegalArgumentException("Field name cannot be empty or null");
				}

				this.fields.add(Pair.of(alias, fieldName));
			}
		}

		return this;
	}

	public SelectTable<?> from(String tableName) {
		return from(null, tableName);
	}

	@SuppressWarnings("unchecked")
	public SelectTable<?> from(String alias, String tableName) {
		return from(Pair.of(alias, tableName));
	}

	@SuppressWarnings("unchecked")
	public SelectTable<?> from(Pair<String, String>... aliasAndTableNames) {
		this.table = new SelectTable<>(this, aliasAndTableNames);

		return this.table;
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		if (this.table == null) {
			throw new IllegalStateException("From was not called so far");
		}

		if (this.fields.isEmpty()) {
			throw new IllegalStateException("Fields cannot be empty");
		}

		StringBuilder strBuilder = new StringBuilder("SELECT ");

		strBuilder.append(this.fields.stream().map(field -> {
			String alias = field.getKey();
			String fieldName = field.getValue();

			String ret = fieldName;
			if (!Objects.equals("*", ret)) {
				ret = DBConnector.getColumnNamesAsString(dbType, fieldName);
			}

			if (StringUtils.isNotBlank(alias)) {
				ret = alias + "." + ret;
			}

			return ret;
		}).collect(Collectors.joining(", ")));
		strBuilder.append(" ");

		strBuilder.append(this.table.build(dbType));
		strBuilder.append(";");

		return strBuilder.toString();
	}
}
