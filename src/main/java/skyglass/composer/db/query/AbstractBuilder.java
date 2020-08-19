package skyglass.composer.db.query;

import skyglass.composer.db.DBConnector;

public abstract class AbstractBuilder {
	protected Query query;

	public AbstractBuilder() {
		this.query = null;
	}

	public AbstractBuilder(Query query) {
		this.query = query;
	}

	void setQuery(Query query) {
		this.query = query;
	}

	abstract String build(DBConnector.DatabaseType dbType);

	public String buildQuery(DBConnector.DatabaseType dbType) {
		if (this.query == null) {
			throw new IllegalStateException("No query was set yet.");
		}

		return this.query.buildQuery(dbType);
	}
}
