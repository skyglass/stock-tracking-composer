package skyglass.composer.db.query;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import skyglass.composer.db.DBConnector;

public class UpdateTable<E extends UpdateTable<E>> extends UpsertTable<E> {
	public static enum JoinType {
		JOIN("JOIN"), INNER_JOIN("INNER JOIN"), CROSS_JOIN("CROSS JOIN"), STRAIGHT_JOIN("STRAIGHT_JOIN"), LEFT_JOIN("LEFT JOIN"), RIGHT_JOIN("RIGHT JOIN"), LEFT_OUTER_JOIN(
				"LEFT OUTER JOIN"), RIGHT_OUTER_JOIN("RIGHT OUTER JOIN");

		private final String asString;

		private JoinType(String asString) {
			this.asString = asString;
		}

		public String getAsString() {
			return asString;
		}
	}

	protected final Map<String, Triple<JoinType, String, JoinConditions>> joinTables = new HashMap<>();

	@SuppressWarnings("unchecked")
	public UpdateTable(Query query, String tableName) {
		this(query, Pair.of(null, tableName));
	}

	@SuppressWarnings("unchecked")
	protected UpdateTable(Query query, Pair<String, String>... aliasAndTableNames) {
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
	public E join(JoinType joinType, String alias, String tableName, JoinConditions conditions) {
		if (this.fromTableNames.size() == 1 && StringUtils.isBlank(this.fromTableNames.get(0).getKey())) {
			throw new UnsupportedOperationException("Missing alias for from table");
		}

		if (joinType == null) {
			throw new IllegalArgumentException("Join type cannot be null");
		}

		if (StringUtils.isAnyBlank(alias, tableName)) {
			throw new IllegalArgumentException("Neither alias nor table name can be empty or null");
		}

		if (conditions == null) {
			throw new IllegalArgumentException("Join conditions cannot be null");
		}

		conditions.setQuery(this.query);

		this.joinTables.put(alias, Triple.of(joinType, tableName, conditions));

		return (E) this;
	}

	@SuppressWarnings("unchecked")
	@Override
	public E where(WhereConditions conditions) {
		return super.where(conditions);
	}

	@Override
	String buildTableName(DBConnector.DatabaseType dbType) {
		Pair<String, String> aliasAndTableName = this.fromTableNames.get(0);
		String ret = DBConnector.getTableName(dbType, aliasAndTableName.getValue());
		if (StringUtils.isNotBlank(aliasAndTableName.getKey())) {
			ret = ret + " " + aliasAndTableName.getKey();
		}

		return ret;
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		StringBuilder strBuilder = new StringBuilder();

		if (!DBConnector.DatabaseType.H2.equals(dbType)) {
			if (this.fromTableNames.size() > 1) {
				int skip = 1;

				strBuilder.append("FROM ");
				strBuilder.append(this.fromTableNames.stream().skip(skip).map(aliasAndTableName -> {
					String alias = aliasAndTableName.getKey();
					String ret = DBConnector.getTableName(dbType, aliasAndTableName.getValue());
					if (StringUtils.isNotBlank(alias)) {
						ret += " " + alias;
					}

					return ret;
				}).collect(Collectors.joining(", ")));
				strBuilder.append(" ");
			}
		}

		if (!this.joinTables.isEmpty()) {
			strBuilder.append(this.joinTables.keySet().stream().map(alias -> {
				Triple<JoinType, String, JoinConditions> join = this.joinTables.get(alias);
				JoinType joinType = join.getLeft();
				String tableName = join.getMiddle();
				JoinConditions conditions = join.getRight();

				return joinType.getAsString() + " " + DBConnector.getTableName(dbType, tableName) + " " + alias + " ON " + conditions.build(dbType);
			}).collect(Collectors.joining(" ")));
			strBuilder.append(" ");
		}

		if (this.whereConditions != null) {
			if (!DBConnector.DatabaseType.H2.equals(dbType)) {
				strBuilder.append("WHERE ");
				strBuilder.append(this.whereConditions.build(dbType));
				strBuilder.append(" ");
			} else {
				strBuilder.append("WHERE ");
				strBuilder.append(this.whereConditions.build(dbType, this.fromTableNames.get(0)));
				strBuilder.append(" ");
			}
		}

		return strBuilder.toString().trim();
	}
}
