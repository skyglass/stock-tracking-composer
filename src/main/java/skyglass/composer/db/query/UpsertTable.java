package skyglass.composer.db.query;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.db.DBConnector;

public class UpsertTable<E extends UpsertTable<E>> extends InsertTable<E> {
	protected WhereConditions whereConditions;

	public UpsertTable(Query query, String tableName) {
		this(query, Pair.of(null, tableName));
	}

	@SafeVarargs
	protected UpsertTable(Query query, Pair<String, String>... aliasAndTableNames) {
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
	public <T extends AbstractTable<T>> T where(WhereConditions conditions) {
		this.whereConditions = conditions;

		if (this.whereConditions != null) {
			this.whereConditions.setQuery(this.query);
		}

		return (T) this;
	}

	private void setAlias(String alias, AbstractWhereCondition condition) {
		if (condition instanceof AbstractBaseWhereCondition) {
			((AbstractBaseWhereCondition) condition).setAlias(alias);
		} else if (condition instanceof WhereConditions) {
			List<AbstractWhereCondition> conditions = ((WhereConditions) condition).getConditions();
			for (AbstractWhereCondition c : conditions) {
				setAlias(alias, c);
			}
		}
	}

	@Override
	String build(DBConnector.DatabaseType dbType) {
		StringBuilder strBuilder = new StringBuilder();

		if (this.whereConditions != null) {
			String tableName = buildTableName(dbType);

			List<AbstractWhereCondition> conditions = this.whereConditions.getConditions();
			for (AbstractWhereCondition condition : conditions) {
				setAlias(tableName, condition);
			}

			strBuilder.append("WHERE ");
			strBuilder.append(this.whereConditions.build(dbType));
			strBuilder.append(" ");
		}

		return strBuilder.toString().trim();
	}
}
