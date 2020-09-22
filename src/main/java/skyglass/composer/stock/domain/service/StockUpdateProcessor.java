package skyglass.composer.stock.domain.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import skyglass.composer.db.exceptions.LockedException;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.exceptions.RunnableWithException;
import skyglass.composer.stock.exceptions.TransactionRollbackException;

public class StockUpdateProcessor {
	private static final Logger log = LoggerFactory.getLogger(StockUpdateProcessor.class);

	private final DataSource dataSource;

	private final StockUpdateConnector stockUpdateConnector;

	public StockUpdateProcessor(DataSource dataSource, StockUpdateConnector stockUpdateConnector) throws IOException {
		this.dataSource = dataSource;
		this.stockUpdateConnector = stockUpdateConnector;
	}

	public void updateStock(Item item, BusinessUnit businessUnit, RunnableWithException<TransactionRollbackException> runnable) throws IOException, SQLException, TransactionRollbackException {

		boolean success = false;
		int retries = 0;
		IOException lastException = null;

		do {
			try {
				stockUpdateConnector.acquireLock(dataSource, item.getUuid(), businessUnit.getUuid());
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
				stockUpdateConnector.releaseLock(dataSource, item.getUuid(), businessUnit.getUuid());
			}
		} while (!success && retries < 2);

		if (!success && lastException != null) {
			throw lastException;
		}
	}

}
