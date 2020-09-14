package skyglass.composer.stock.domain.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import skyglass.composer.db.exceptions.LockedException;
import skyglass.composer.stock.domain.model.Stock;

@Repository
public class StockUpdateConnector {
	private static final Logger log = LoggerFactory.getLogger(StockUpdateConnector.class);

	public static final String KEY_LOCK_TABLE_NAME = "keylock";

	private static final long ACQUIRE_LOCK_TIMEOUT = 30000;

	private static final long RELEASE_LOCK_TIMEOUT = 40000;

	public void acquireLock(DataSource dataSource, String itemUuid, String businessUnitUuid)
			throws LockedException, SQLException, InterruptedException {
		if (StringUtils.isAnyBlank(itemUuid, businessUnitUuid)) {
			throw new IllegalArgumentException("Neither businessUnitUuid, nor itemUuid can be null or empty");
		}

		int maxRetries = 800;
		int retry = 0;
		long start = System.currentTimeMillis();

		do {
			retry++;

			try {
				_acquireLock(dataSource, itemUuid, businessUnitUuid);
				return;
			} catch (LockedException ex) {
				if (System.currentTimeMillis() > start + ACQUIRE_LOCK_TIMEOUT) {
					break;
				}
				Thread.sleep(50L);
			}

		} while (retry < maxRetries);

		log.error("Giving up after " + retry + " retries, could not aquire lock!");

		throw new LockedException();
	}

	private void _acquireLock(DataSource dataSource, String itemUuid, String businessUnitUuid)
			throws LockedException, SQLException {
		try (Connection dbConnection = dataSource.getConnection()) {

			String key = Stock.key(itemUuid, businessUnitUuid);

			try {
				String insertQuery = "INSERT INTO KEYLOCK VALUES(?)";

				try (PreparedStatement createLockStmt = dbConnection.prepareStatement(insertQuery)) {
					createLockStmt.setString(1, key);
					createLockStmt.execute();
				}
			} catch (SQLException ex) {
				throw new LockedException(ex);
			}

		}

	}

	public void releaseLock(DataSource dataSource, String itemUuid, String businessUnitUuid) throws SQLException {
		if (StringUtils.isAnyBlank(itemUuid, businessUnitUuid)) {
			throw new IllegalArgumentException("Neither itemUuid, nor businessUnitUuid can be null or empty");
		}

		int maxRetries = 1600;
		int retry = 0;
		long start = System.currentTimeMillis();

		do {
			retry++;

			try {
				_releaseLock(dataSource, itemUuid, businessUnitUuid);
				return;
			} catch (SQLException ex) {
				if (System.currentTimeMillis() > start + RELEASE_LOCK_TIMEOUT) {
					break;
				}
				try {
					Thread.sleep(10L);
				} catch (InterruptedException e) {
					if (log.isDebugEnabled()) {
						log.debug("Sleep was interrupted?!", e);
					}
				}
			}
		} while (retry < maxRetries);

		log.error("Giving up after " + retry + " retries, could not release lock!");
	}

	private void _releaseLock(DataSource dataSource, String itemUuid, String businessUnitUuid) throws SQLException {
		try (Connection dbConnection = dataSource.getConnection()) {

			String key = Stock.key(itemUuid, businessUnitUuid);

			try (PreparedStatement deleteLockStmt = dbConnection.prepareStatement("DELETE FROM KEYLOCK WHERE KEY = ?")) {
				deleteLockStmt.setString(1, key);
				deleteLockStmt.execute();
			}
		}
	}


}
