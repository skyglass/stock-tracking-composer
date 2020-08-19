package skyglass.composer.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnector {
	private static final Logger log = LoggerFactory.getLogger(DBConnector.class);

	public static enum DatabaseType {
		POSTGRE_SQL("postgresql"), H2("h2"), UNKNOWN(null);

		private final String name;

		private DatabaseType(String name) {
			this.name = name;
		}

		public String getName() {
			return this.name;
		}
	};

	private DBConnector() {
	}

	public static void startTransaction(Connection dbConnection) throws SQLException {
		if (!dbConnection.getAutoCommit()) {
			commitTransaction(dbConnection);
		}

		dbConnection.setAutoCommit(false);
	}

	public static void commitTransaction(Connection dbConnection) throws SQLException {
		dbConnection.commit();

		dbConnection.setAutoCommit(true);
	}

	public static String getColumnName(Connection dbConnection, String rawColumnName) throws SQLException {
		if (StringUtils.isBlank(rawColumnName)) {
			return rawColumnName;
		}

		return getColumnName(getDatabaseType(dbConnection), rawColumnName);
	}

	public static String getColumnName(DatabaseType dbType, String rawColumnName) {
		if (StringUtils.isBlank(rawColumnName)) {
			return rawColumnName;
		}

		String columnName = rawColumnName;

		switch (dbType) {
			case H2:
				columnName = rawColumnName.toUpperCase();
				break;
			case POSTGRE_SQL:
			case UNKNOWN:
				break;
		}

		return columnName;
	}

	public static String[] getColumnNames(Connection dbConnection, String... rawColumnNames) throws SQLException {
		if (rawColumnNames == null || rawColumnNames.length == 0) {
			return rawColumnNames;
		}

		return getColumnNames(getDatabaseType(dbConnection), rawColumnNames);
	}

	public static String[] getColumnNames(DatabaseType dbType, String... rawColumnNames) {
		if (rawColumnNames == null || rawColumnNames.length == 0) {
			return rawColumnNames;
		}

		String[] columnNames = new String[rawColumnNames.length];
		for (int i = 0; i < rawColumnNames.length; i++) {
			columnNames[i] = getColumnName(dbType, rawColumnNames[i]);
		}

		return columnNames;
	}

	public static String getColumnNamesAsString(Connection dbConnection, String... rawColumnNames) throws SQLException {
		return getColumnNamesAsString(getDatabaseType(dbConnection), rawColumnNames);
	}

	public static String getColumnNamesAsString(DatabaseType dbType, String... rawColumnNames) {
		String[] columnNames = getColumnNames(dbType, rawColumnNames);
		if (columnNames != null && columnNames.length > 0) {
			switch (dbType) {
				case H2:
					return Arrays.asList(columnNames).stream().collect(Collectors.joining(", "));
				default:
					return "\"" + Arrays.asList(columnNames).stream().collect(Collectors.joining("\", \"")) + "\"";
			}
		}

		return "";
	}

	public static String getTableName(Connection dbConnection, String rawTableName) throws SQLException {
		if (StringUtils.isBlank(rawTableName)) {
			return rawTableName;
		}

		return getTableName(getDatabaseType(dbConnection), rawTableName);
	}

	public static String getTableName(DatabaseType dbType, String rawTableName) {
		if (StringUtils.isBlank(rawTableName)) {
			return rawTableName;
		}

		String tableName = rawTableName;

		switch (dbType) {
			case POSTGRE_SQL:
			case UNKNOWN:
				tableName = "\"" + rawTableName + "\"";
				break;
			case H2:
				tableName = rawTableName.toUpperCase();
				break;
		}

		return tableName;
	}

	public static String getDialect(Connection dbConnection) throws SQLException {
		if (dbConnection != null) {
			DatabaseMetaData metaData = dbConnection.getMetaData();
			if (metaData != null) {
				return metaData.getDatabaseProductName() + " (" + metaData.getDatabaseProductVersion() + ")";
			}
		}

		return null;
	}

	public static DatabaseType getDatabaseType(Connection dbConnection) throws SQLException {
		if (dbConnection != null) {
			DatabaseMetaData metaData = dbConnection.getMetaData();
			if (metaData != null) {
				String databaseName = metaData.getDatabaseProductName();
				if (StringUtils.isNotBlank(databaseName)) {
					databaseName = databaseName.trim().toLowerCase();

					if (databaseName.startsWith(DatabaseType.POSTGRE_SQL.getName())) {
						return DatabaseType.POSTGRE_SQL;
					} else if (databaseName.startsWith(DatabaseType.H2.getName())) {
						return DatabaseType.H2;
					} else {
						log.warn("Found unsupported database " + databaseName + " (" + metaData.getDatabaseProductVersion() + ")");
					}
				}
			}
		}

		return DatabaseType.UNKNOWN;
	}
}
