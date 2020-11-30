package skyglass.composer.query.builder;

import java.util.Arrays;
import java.util.List;

import skyglass.composer.query.request.ColumnId;
import skyglass.composer.query.request.TableType;

public class QueryResultDefinition {

	private TableType table;

	private final List<ColumnId> columns;

	public QueryResultDefinition(TableType table, ColumnId... columns) {
		this.table = table;
		this.columns = Arrays.asList(columns);
	}

	public List<ColumnId> getColumns() {
		return columns;
	}

	public TableType getTable() {
		return table;
	}

	public String getTableName() {
		return table.getTableName();
	}

	public String getTableAlias() {
		return table.getTableAlias();
	}

}
