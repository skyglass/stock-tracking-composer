package skyglass.composer.stock.update.domain.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.db.DBConnector;
import skyglass.composer.db.exceptions.LockedException;

public class StockUpdateProcessor {
	private static final Logger log = LoggerFactory.getLogger(StockUpdateProcessor.class);

	private final DataSource dataSource;

	private final StockUpdateConnector stockUpdateConnector;

	private final DBConnector.DatabaseType dbType;

	public StockUpdateProcessor(DataSource dataSource, StockUpdateConnector stockUpdateConnector) throws IOException {
		this.dataSource = dataSource;
		this.stockUpdateConnector = stockUpdateConnector;

		try (Connection dbConnection = this.dataSource.getConnection()) {
			this.dbType = DBConnector.getDatabaseType(dbConnection);

			if (log.isInfoEnabled()) {
				log.info("DATABASE DIALECT: " + DBConnector.getDialect(dbConnection) + " => " + this.dbType);
			}
		} catch (SQLException ex) {
			throw new IOException(ex);
		}
	}

	public void updateStock(String businessUnitUuid, String itemUuid, Runnable runnable) throws IOException, SQLException {

		boolean success = false;
		int retries = 0;
		IOException lastException = null;

		do {
			try {
				stockUpdateConnector.acquireLock(dataSource, businessUnitUuid, itemUuid);
				runnable.run();
				success = true;
			} catch (LockedException ex) {
				log.error("Could not acquire lock!", ex);

				lastException = new IOException(ex);
			} catch (InterruptedException ex) {
				log.warn("Acquire lock was interrupted", ex);

				lastException = new IOException(ex);
			} finally {
				retries++;
				stockUpdateConnector.releaseLock(dataSource, businessUnitUuid, itemUuid);
			}
		} while (!success && retries < 2);

		if (!success && lastException != null) {
			throw lastException;
		}
	}

}
