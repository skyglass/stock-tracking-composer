package skyglass.composer.db.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public abstract class AbstractTable<E extends AbstractTable<E>> extends AbstractBuilder {
	protected final List<Pair<String, String>> fromTableNames = new ArrayList<>();

	public AbstractTable(Query query, String tableName) {
		this(query, null, tableName);
	}

	public AbstractTable(Query query, String alias, String tableName) {
		this(query, Pair.of(alias, tableName));
	}

	@SafeVarargs
	public AbstractTable(Query query, Pair<String, String>... aliasAndTableNames) {
		if (aliasAndTableNames == null || aliasAndTableNames.length == 0) {
			throw new IllegalArgumentException("List of aliases and table names cannot be empty or null");
		}

		if (query == null) {
			throw new IllegalArgumentException("Query cannot be null");
		}

		this.query = query;

		for (Pair<String, String> aliasAndTableName : aliasAndTableNames) {
			String alias = aliasAndTableName.getKey();
			String tableName = aliasAndTableName.getValue();

			if (StringUtils.isBlank(tableName)) {
				throw new IllegalArgumentException("Table name cannot be empty or null");
			}

			this.fromTableNames.add(Pair.of(alias, tableName));
		}
	}

	public E addTableName(String tableName) {
		return addTableName(null, tableName);
	}

	@SuppressWarnings("unchecked")
	public E addTableName(String alias, String tableName) {
		if (StringUtils.isBlank(tableName)) {
			throw new IllegalArgumentException("Table name cannot be empty or null");
		}

		return addTableNames(Pair.of(alias, tableName));
	}

	@SuppressWarnings("unchecked")
	public E addTableNames(Pair<String, String>... aliasAndTableNames) {
		if (aliasAndTableNames == null || aliasAndTableNames.length == 0) {
			return (E) this;
		}

		for (Pair<String, String> aliasAndTableName : aliasAndTableNames) {
			String alias = aliasAndTableName.getKey();
			String tableName = aliasAndTableName.getValue();
			if (StringUtils.isBlank(tableName)) {
				throw new IllegalArgumentException("Table name cannot be empty or null");
			}

			this.fromTableNames.add(Pair.of(alias, tableName));
		}

		return (E) this;
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		StringBuilder strBuilder = new StringBuilder("FROM ");

		strBuilder.append(this.fromTableNames.stream().map(aliasAndTableName -> {
			String alias = aliasAndTableName.getKey();
			String ret = DBConnector.getTableName(dbType, aliasAndTableName.getValue());
			if (StringUtils.isNotBlank(alias)) {
				ret += " " + alias;
			}

			return ret;
		}).collect(Collectors.joining(", ")));
		strBuilder.append(" ");

		return strBuilder.toString().trim();
	}
}
