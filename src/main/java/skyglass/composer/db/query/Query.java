package skyglass.composer.db.query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.db.DBConnector;

public abstract class Query extends AbstractBuilder {
	private static final Logger log = LoggerFactory.getLogger(Query.class);

	@Override
	public String buildQuery(DBConnector.DatabaseType dbType) {
		String queryString = build(dbType);

		if (log.isDebugEnabled()) {
			log.debug("Built query: " + queryString);
		}

		return queryString;
	}
}
