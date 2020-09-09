package skyglass.composer.stock.domain.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockParameter;
import skyglass.composer.stock.exceptions.AlreadyExistsException;
import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.EntityUtil;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockEntity;
import skyglass.composer.stock.persistence.entity.StockHistoryEntity;
import skyglass.composer.stock.persistence.entity.StockMessageEntity;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class StockHistoryBean extends AEntityBean<StockHistoryEntity> {

	@Autowired
	private DataSource dataSource;

	@Override
	public StockHistoryEntity findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery("SELECT sh FROM StockHistoryEntity sh WHERE svh.uuid = :uuid", StockHistoryEntity.class);
		query.setParameter("uuid", uuid);
		StockHistoryEntity stockHistory = EntityUtil.getSingleResultSafely(query);
		if (stockHistory == null) {
			throw new NotAccessibleException(StockHistoryEntity.class, uuid);
		}
		return stockHistory;
	}

	public List<StockHistoryEntity> findForPeriod(Item item, BusinessUnit businessUnit, Date startDate, Date endDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.businessUnit.uuid = :businessUnitUuid "
				+ (endDate == null ? "" : "AND sh.startDate < :endDate ")
				+ (startDate == null ? "" : "AND (sh.endDate IS NULL OR sh.endDate > :startDate) ")
				+ "ORDER BY sh.startDate DESC";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("businessUnitUuid", businessUnit.getUuid());
		if (startDate != null) {
			query.setParameter("startDate", startDate);
		}
		if (endDate != null) {
			query.setParameter("endDate", endDate);
		}
		return EntityUtil.getListResultSafely(query);
	}

	public List<StockHistoryEntity> find(Item item, BusinessUnit businessUnit) {
		TypedQuery<StockHistoryEntity> query = findQuery(item, businessUnit);
		return EntityUtil.getListResultSafely(query);
	}

	public StockHistoryEntity findLatest(Item item, BusinessUnit businessUnit) {
		TypedQuery<StockHistoryEntity> query = findQuery(item, businessUnit);
		query.setMaxResults(1);
		List<StockHistoryEntity> result = EntityUtil.getListResultSafely(query);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	private TypedQuery<StockHistoryEntity> findQuery(Item item, BusinessUnit businessUnit) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.businessUnit.uuid = :businessUnitUuid ORDER BY sh.startDate DESC";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("businessUnitUuid", businessUnit.getUuid());
		return query;
	}

	@Override
	public StockHistoryEntity create(StockHistoryEntity entity)
			throws AlreadyExistsException, IllegalArgumentException, IllegalStateException, NotAccessibleException {
		throw new UnsupportedOperationException("create method is not supported. Please, only use createHistoryForBusinessUnitAndItem(...) methods for creation of stock history entities");
	}

	@Override
	public StockHistoryEntity update(StockHistoryEntity entity)
			throws AlreadyExistsException, IllegalArgumentException, IllegalStateException, NotAccessibleException {
		throw new UnsupportedOperationException("update method is not supported. Please, only use createHistoryForBusinessUnitAndItem(...) methods for update of stock history entities");
	}

	public StockHistoryEntity createHistory(StockEntity stock, StockMessageEntity stockMessage, ItemEntity item, BusinessUnitEntity businessUnit, boolean isFrom) {
		return createHistoryForItemAndBusinessUnit(item, businessUnit, stock, stockMessage, isFrom);
	}

	@NotNull
	private StockHistoryEntity createHistoryForItemAndBusinessUnit(ItemEntity item, BusinessUnitEntity businessUnit, StockEntity stock, StockMessageEntity stockMessage, boolean isFrom) {
		Date validityDate = stockMessage.getCreatedAt();
		double doubleValue = stock.getAmount();
		double delta = isFrom ? -stockMessage.getAmount() : stockMessage.getAmount();

		List<StockHistoryEntity> previousList = findValidPreviousList(item, businessUnit, validityDate);
		StockHistoryEntity previous = null;
		if (CollectionUtils.isNotEmpty(previousList)) {
			//If more  than one history item found, it means the data is corrupted
			//The only thing we could do in this case is to clean it, so there will be no ambiguity in methods, which depend on stock history unique constraint:
			//Stock History Unique Constraint: For each validityDate, one and only one stock history element must be found
			if (previousList.size() > 1) {
				throw new RuntimeException(
						"Stock History Unique Constraint Exception: For each validityDate, one and only one stock history element must be found. Please, fix the code!!!");
			}
			previous = previousList.get(0);
		}

		StockHistoryEntity valid = new StockHistoryEntity(null, item, businessUnit, doubleValue, validityDate, previous != null ? previous.getEndDate() : null,
				StockParameter.copyList(stockMessage.getParameters()));

		if (previous != null) {
			previous.setEndDate(validityDate);
			merge(previous);
		}

		StockHistoryEntity next = findValidNext(item, businessUnit, validityDate);

		if (next != null) {
			valid.setEndDate(next.getStartDate());
			updateNextStock(item, businessUnit, next.getStartDate(), delta);
		}

		//if previous start date equals validity date, then the previous end date also becomes equal to validity date. It means that the new stock history interval completely replaces previous interval. Therefore, previous interval should be deleted.
		//It doesn't make sense to keep interval with the same start and end date in the history anyway
		if (previous != null && previous.getStartDate().equals(previous.getEndDate())) {
			remove(previous);
		}

		return persist(valid);
	}

	private List<StockHistoryEntity> findValidPreviousList(ItemEntity item, BusinessUnitEntity businessUnit, Date validityDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.businessUnit.uuid = :businessUnitUuid AND sh.startDate <= :validityDate AND (sh.endDate IS NULL OR sh.endDate > :validityDate) ORDER BY sh.startDate";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("businessUnitUuid", businessUnit.getUuid());
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	private StockHistoryEntity findValidNext(ItemEntity item, BusinessUnitEntity businessUnit, Date validityDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.businessUnit.uuid = :businessUnitUuid AND sh.startDate > :validityDate ORDER BY sh.startDate";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("businessUnitUuid", businessUnit.getUuid());
		query.setParameter("validityDate", validityDate);
		query.setMaxResults(1);
		return EntityUtil.getSingleResultSafely(query);
	}

	private void updateNextStock(ItemEntity item, BusinessUnitEntity businessUnit, Date validityDate, double amount) {
		String queryStr = "UPDATE StockHistory SET amount = amount + ? WHERE item_uuid = ? AND businessUnit_uuid = ? AND startDate >= ?";
		try {
			try (Connection dbConnection = dataSource.getConnection()) {
				try (PreparedStatement updateStmt = dbConnection.prepareStatement(queryStr)) {
					updateStmt.setDouble(1, amount);
					updateStmt.setString(2, item.getUuid());
					updateStmt.setString(3, businessUnit.getUuid());
					updateStmt.setTimestamp(4, new Timestamp(validityDate.getTime()));
					updateStmt.execute();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
