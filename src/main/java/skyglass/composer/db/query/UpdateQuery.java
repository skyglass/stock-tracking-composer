package skyglass.composer.db.query;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class UpdateQuery extends Query {

	private final UpdateTable<?> table;

	private EqualsCondition[] conditions;

	public UpdateQuery(String tableName, EqualsCondition... conditions) {
		this(null, tableName, conditions);
	}

	@SuppressWarnings("unchecked")
	public UpdateQuery(String alias, String tableName, EqualsCondition... conditions) {
		if (conditions == null) {
			conditions = new EqualsCondition[0];
		}

		this.table = new UpdateTable<>(this, Pair.of(alias, tableName));
		this.conditions = conditions;
	}

	public UpdateQuery update(EqualsCondition... conditions) {
		if (conditions != null && conditions.length > 0) {
			this.conditions = ArrayUtils.addAll(this.conditions, conditions);
		}

		return this;
	}

	@SuppressWarnings("unchecked")
	public UpdateTable<?> from(String alias, String tableName) {
		return from(Pair.of(alias, tableName));
	}

	@SuppressWarnings("unchecked")
	public UpdateTable<?> from(Pair<String, String>... aliasAndTableNames) {
		return this.table.addTableNames(aliasAndTableNames);
	}

	public UpdateTable<?> join(UpdateTable.JoinType joinType, String alias, String tableName, JoinConditions conditions) {
		return this.table.join(joinType, alias, tableName, conditions);
	}

	public UpdateTable<?> where(WhereConditions conditions) {
		return this.table.where(conditions);
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		if (conditions == null || conditions.length == 0) {
			throw new IllegalStateException("Conditions cannot be empty or null");
		}

		StringBuilder strBuilder = new StringBuilder("UPDATE ");
		strBuilder.append(this.table.buildTableName(dbType));
		strBuilder.append(" ");
		strBuilder.append("SET ");
		strBuilder.append(Arrays.asList(this.conditions).stream().map(condition -> condition.buildForUpdateSet(dbType, table.fromTableNames, table.whereConditions)).collect(Collectors.joining(", ")));
		strBuilder.append(" ");
		strBuilder.append(this.table.build(dbType));
		strBuilder.append(";");

		return strBuilder.toString();
	}
}
