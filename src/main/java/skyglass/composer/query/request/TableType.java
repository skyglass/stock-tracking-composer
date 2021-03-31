package skyglass.composer.query.request;

public enum TableType {

	StockMessage("StockMessage", "sm"), //
	StockHistory("StockHistory", "sh"), //
	CustomParameter("CustomParameter", "cp"), //
	Stock("Stock", "st"), //
	Context("Context", "ctx"), //
	User("\"USER\"", "u");

	private final String tableName;

	private final String tableAlias;

	private TableType(String tableName, String tableAlias) {
		this.tableName = tableName;
		this.tableAlias = tableAlias;
	}

	public String getTableName() {
		return tableName;
	}

	public String getTableAlias() {
		return tableAlias;
	}

	public String getUuidPath() {
		return tableAlias + ".uuid";
	}

}
