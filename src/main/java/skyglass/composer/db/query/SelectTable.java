package skyglass.composer.db.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import skyglass.composer.db.DBConnector;

public class SelectTable<E extends SelectTable<E>> extends UpdateTable<E> {
	public static enum OrderDirection {
		ASC, DESC;
	}

	protected final List<Triple<String, String, OrderDirection>> orderByFieldNames = new ArrayList<>();

	protected Integer limit;

	protected Integer offset;

	public SelectTable(Query query, String tableName) {
		this(query, null, tableName);
	}

	public SelectTable(Query query, String alias, String tableName) {
		this(query, Pair.of(alias, tableName));
	}

	@SafeVarargs
	public SelectTable(Query query, Pair<String, String>... aliasAndTableNames) {
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

	@Override
	public E where(WhereConditions conditions) {
		return (E) super.where(conditions);
	}

	public E orderBy(String fieldName) {
		return orderBy(null, fieldName);
	}

	public E orderBy(String alias, String fieldName) {
		return orderBy(alias, fieldName, null);
	}

	public E orderBy(String fieldName, OrderDirection orderDirection) {
		return orderBy(null, fieldName, orderDirection);
	}

	@SuppressWarnings("unchecked")
	public E orderBy(String alias, String fieldName, OrderDirection orderDirection) {
		if (StringUtils.isBlank(fieldName)) {
			throw new IllegalArgumentException("Field name cannot be empty or null");
		}

		if (orderDirection == null) {
			orderDirection = OrderDirection.ASC;
		}

		this.orderByFieldNames.add(Triple.of(alias, fieldName, orderDirection));

		return (E) this;
	}

	@SuppressWarnings("unchecked")
	public E offset(Integer offset) {
		this.offset = offset;

		return (E) this;
	}

	@SuppressWarnings("unchecked")
	public E limit(Integer limit) {
		this.limit = limit;

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
			strBuilder.append("WHERE ");
			strBuilder.append(this.whereConditions.build(dbType));
			strBuilder.append(" ");
		}

		if (!this.orderByFieldNames.isEmpty()) {
			strBuilder.append("ORDER BY ");
			strBuilder.append(this.orderByFieldNames.stream().map(order -> {
				String alias = order.getLeft();
				String fieldName = order.getMiddle();
				OrderDirection dir = order.getRight();

				return (StringUtils.isNotBlank(alias) ? alias + "." : "") + DBConnector.getColumnNamesAsString(dbType, fieldName) + " " + dir;
			}).collect(Collectors.joining(", ")));
			strBuilder.append(" ");
		}

		if (this.offset != null) {
			strBuilder.append("OFFSET ");
			strBuilder.append(this.offset);
			strBuilder.append(" ");
		}

		if (this.limit != null) {
			strBuilder.append("LIMIT ");
			strBuilder.append(this.limit);
			strBuilder.append(" ");
		}

		return strBuilder.toString().trim();
	}
}
