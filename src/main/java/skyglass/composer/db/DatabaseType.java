package skyglass.composer.db;

public enum DatabaseType {
	SAP_HANA("hdb"), POSTGRE_SQL("postgresql"), H2("h2"), UNKNOWN(null);

	private final String name;

	private DatabaseType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
};
