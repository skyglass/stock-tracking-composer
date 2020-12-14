package skyglass.composer.stock.entity.repository;

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

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.domain.factory.StockParameterFactory;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockHistoryEntity;
import skyglass.composer.stock.exceptions.NotAccessibleException;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class StockHistoryRepository extends AEntityRepository<StockHistoryEntity> {

	@Autowired
	private DataSource dataSource;

	@Autowired
	private StockParameterFactory stockParameterFactory;

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

	public List<StockHistoryEntity> findForPeriod(Item item, Context context, Date startDate, Date endDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.context.uuid = :contextUuid "
				+ (endDate == null ? "" : "AND (sh.startDate IS NULL OR sh.startDate < :endDate) ")
				+ (startDate == null ? "" : "AND (sh.endDate IS NULL OR sh.endDate > :startDate) ")
				+ "ORDER BY sh.startDate DESC";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("contextUuid", context.getUuid());
		if (startDate != null) {
			query.setParameter("startDate", startDate);
		}
		if (endDate != null) {
			query.setParameter("endDate", endDate);
		}
		return EntityUtil.getListResultSafely(query);
	}

	public List<StockHistoryEntity> find(Item item, Context context) {
		TypedQuery<StockHistoryEntity> query = findQuery(item, context);
		return EntityUtil.getListResultSafely(query);
	}

	public StockHistoryEntity findLatest(Item item, Context context) {
		TypedQuery<StockHistoryEntity> query = findQuery(item, context);
		query.setMaxResults(1);
		List<StockHistoryEntity> result = EntityUtil.getListResultSafely(query);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	private TypedQuery<StockHistoryEntity> findQuery(Item item, Context context) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.context.uuid = :contextUuid ORDER BY sh.startDate DESC";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("contextUuid", context.getUuid());
		return query;
	}

	public StockHistoryEntity createHistory(StockMessage stockMessage, ItemEntity item, ContextEntity context, double delta) {
		return createHistoryForItemAndContext(item, context, stockMessage, delta);
	}

	@NotNull
	private StockHistoryEntity createHistoryForItemAndContext(ItemEntity item, ContextEntity context, StockMessage stockMessage, double delta) {
		Date validityDate = stockMessage.getCreatedAt();

		List<StockHistoryEntity> previousList = findValidPreviousList(item, context, validityDate);
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

		double currentValue = previous != null ? previous.getAmount() : 0;

		StockHistoryEntity valid = new StockHistoryEntity(null, item, context, currentValue + delta, validityDate, previous != null ? previous.getEndDate() : null,
				stockParameterFactory.copyObjectList(stockMessage.getParameters()));

		StockHistoryEntity next = findValidNext(item, context, validityDate);

		if (previous != null) {
			previous.setEndDate(validityDate);
			updateEntity(previous);
		}

		if (next != null) {
			valid.setEndDate(next.getStartDate());
			updateNextStock(item, context, next.getStartDate(), delta);
		}

		//if previous start date equals validity date, then the previous end date also becomes equal to validity date. It means that the new stock history interval completely replaces previous interval. Therefore, previous interval should be deleted.
		//It doesn't make sense to keep interval with the same start and end date in the history anyway
		if (previous != null && previous.getStartDate() != null && previous.getStartDate().equals(previous.getEndDate())) {
			deleteEntity(previous);
		}

		if (previous == null) {
			previous = new StockHistoryEntity(null, item, context, 0D, null, validityDate, null);
			createEntity(previous);
		}

		return createEntity(valid);
	}

	private List<StockHistoryEntity> findValidPreviousList(ItemEntity item, ContextEntity context, Date validityDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.context.uuid = :contextUuid AND (sh.startDate IS NULL OR sh.startDate <= :validityDate) AND (sh.endDate IS NULL OR sh.endDate > :validityDate) ORDER BY sh.startDate";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("contextUuid", context.getUuid());
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	private StockHistoryEntity findValidNext(ItemEntity item, ContextEntity context, Date validityDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.item.uuid = :itemUuid AND sh.context.uuid = :contextUuid AND sh.startDate > :validityDate ORDER BY sh.startDate";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("contextUuid", context.getUuid());
		query.setParameter("validityDate", validityDate);
		query.setMaxResults(1);
		return EntityUtil.getSingleResultSafely(query);
	}

	private void updateNextStock(ItemEntity item, ContextEntity context, Date validityDate, double amount) {
		String queryStr = "UPDATE StockHistory SET amount = amount + ? WHERE item_uuid = ? AND context_uuid = ? AND startDate >= ?";
		try {
			try (Connection dbConnection = dataSource.getConnection()) {
				try (PreparedStatement updateStmt = dbConnection.prepareStatement(queryStr)) {
					updateStmt.setDouble(1, amount);
					updateStmt.setString(2, item.getUuid());
					updateStmt.setString(3, context.getUuid());
					updateStmt.setTimestamp(4, new Timestamp(validityDate.getTime()));
					updateStmt.execute();
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
