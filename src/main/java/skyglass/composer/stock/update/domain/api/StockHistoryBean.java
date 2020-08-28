package skyglass.composer.stock.update.domain.api;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.Stock;
import skyglass.composer.stock.domain.StockMessage;
import skyglass.composer.stock.domain.StockParameter;
import skyglass.composer.stock.exceptions.AlreadyExistsException;
import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.EntityUtil;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockHistoryEntity;

@Repository
@Transactional
public class StockHistoryBean extends AEntityBean<StockHistoryEntity> {

	@Autowired
	private ItemBean itemBean;

	@Autowired
	private BusinessUnitBean businessUnitBean;

	@Override
	public StockHistoryEntity findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		TypedQuery<StockHistoryEntity> query = buildQuery("SELECT sh FROM StockHistoryEntity sh WHERE svh.uuid = :uuid");
		query.setParameter("uuid", uuid);
		StockHistoryEntity stockHistory = EntityUtil.getSingleResultSafely(query);
		if (stockHistory == null) {
			throw new NotAccessibleException(StockHistoryEntity.class, uuid);
		}
		return stockHistory;
	}

	public List<StockHistoryEntity> findByBusinessUnitAndItemUuid(String itemUuid, String businessUnitUuid) {
		ItemEntity item = itemBean.findByUuidSecure(itemUuid);
		BusinessUnitEntity businessUnit = businessUnitBean.findByUuidSecure(businessUnitUuid);

		TypedQuery<StockHistoryEntity> query = buildQuery("SELECT sh FROM StockHistoryEntity sh WHERE sh.businessUnit = :businessUnit AND sh.item = :item ORDER BY sh.startDate DESC");
		query.setParameter("item", item);
		query.setParameter("businessUnit", businessUnit);

		List<StockHistoryEntity> stockHistory = EntityUtil.getListResultSafely(query);
		return stockHistory;
	}

	private TypedQuery<StockHistoryEntity> buildQuery(String queryStr) {
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		return query;
	}

	@NotNull
	public Collection<StockHistoryEntity> getStockHistory(ItemEntity item, BusinessUnitEntity businessUnit, Date fromDate, Date toDate) {
		String whereExtension = "sh.businessUnit = :businessUnit AND sh.item = :item";

		if (toDate != null) {
			whereExtension += " AND sh.startDate <= :toDate";
		}
		if (fromDate != null) {
			whereExtension += " AND sh.endDate >= :fromDate";
		}

		whereExtension += " ORDER BY sh.startDate DESC";

		TypedQuery<StockHistoryEntity> query = buildQuery("SELECT sh FROM StockHistoryEntity sh WHERE " + whereExtension);

		if (fromDate != null) {
			query.setParameter("fromDate", fromDate);
		}
		if (toDate != null) {
			query.setParameter("toDate", toDate);
		}

		query.setParameter("businessUnit", businessUnit);
		query.setParameter("item", item);
		return EntityUtil.getListResultSafely(query);
	}

	public List<StockHistoryEntity> find(ItemEntity item, BusinessUnitEntity businessUnit) {
		TypedQuery<StockHistoryEntity> query = findQuery(item, businessUnit);
		return EntityUtil.getListResultSafely(query);
	}

	public StockHistoryEntity findLatest(ItemEntity item, BusinessUnitEntity businessUnit) {
		TypedQuery<StockHistoryEntity> query = findQuery(item, businessUnit);
		query.setMaxResults(1);
		List<StockHistoryEntity> result = EntityUtil.getListResultSafely(query);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	private TypedQuery<StockHistoryEntity> findQuery(ItemEntity item, BusinessUnitEntity businessUnit) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.businessUnit = :businessUnit AND sh.item = :item ORDER BY sh.startDate DESC";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("item", item);
		query.setParameter("businessUnit", businessUnit);
		return query;
	}

	public StockHistoryEntity findValid(ItemEntity item, BusinessUnitEntity businessUnit, Date validityDate) {
		List<StockHistoryEntity> result = findValidPreviousList(item, businessUnit, validityDate);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	private List<StockHistoryEntity> findValidPreviousList(ItemEntity item, BusinessUnitEntity businessUnit, Date validityDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.businessUnit = :businessUnit AND sh.item = :item AND sh.startDate <= :validityDate AND (sh.endDate IS NULL OR sh.endDate > :validityDate) ORDER BY sh.startDate";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("businessUnit", businessUnit);
		query.setParameter("item", item);
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	private List<StockHistoryEntity> findValidNextList(ItemEntity item, BusinessUnitEntity businessUnit, Date validityDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.sensor.uuid = :sensorUuid AND sh.startDate > :validityDate ORDER BY sh.startDate";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("businessUnit", businessUnit);
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	public List<StockHistoryEntity> findValidListForPeriod(ItemEntity item, BusinessUnitEntity businessUnit, Date startDate, Date endDate) {
		String queryStr = "SELECT sh FROM StockHistoryEntity sh WHERE sh.businessUnit = :businessUnit AND sh.item = :item "
				+ (endDate == null ? "" : "AND sh.startDate < :endDate ")
				+ (startDate == null ? "" : "AND (sh.endDate IS NULL OR sh.endDate > :startDate) ")
				+ "ORDER BY sh.startDate DESC";
		TypedQuery<StockHistoryEntity> query = entityBeanUtil.createQuery(queryStr, StockHistoryEntity.class);
		query.setParameter("businessUnit", businessUnit);
		query.setParameter("item", item);
		if (startDate != null) {
			query.setParameter("startDate", startDate);
		}
		if (endDate != null) {
			query.setParameter("endDate", endDate);
		}
		return EntityUtil.getListResultSafely(query);
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

	public StockHistoryEntity createHistory(Stock stock, StockMessage stockMessage) {
		BusinessUnitEntity businessUnit = entityBeanUtil.find(BusinessUnitEntity.class, stock.getBusinessUnit().getUuid());
		ItemEntity item = entityBeanUtil.find(ItemEntity.class, stock.getBusinessUnit().getUuid());
		return createHistoryForItemAndBusinessUnit(item, businessUnit, stock, stockMessage);
	}

	@NotNull
	private StockHistoryEntity createHistoryForItemAndBusinessUnit(ItemEntity item, BusinessUnitEntity businessUnit, Stock stock, StockMessage stockMessage) {
		Date validityDate = stockMessage.getCreatedAt();
		double doubleValue = stock.getAmount();

		List<StockHistoryEntity> previousList = findValidPreviousList(item, businessUnit, validityDate);
		StockHistoryEntity previous = null;
		if (CollectionUtils.isNotEmpty(previousList)) {
			//If more  than one history item found, it means the data is corrupted
			//The only thing we could do in this case is to clean it, so there will be no ambiguity in methods, which depend on stock history unique constraint:
			//Stock History Unique Constraint: For each validityDate, one and only one stock history element must be found
			if (previousList.size() > 1) {
				for (int i = 1; i < previousList.size(); i++) {
					remove(previousList.get(i));
				}
			}
			previous = previousList.get(0);
		}

		StockHistoryEntity valid = new StockHistoryEntity(null, item, businessUnit, doubleValue, validityDate, previous != null ? previous.getEndDate() : null,
				StockParameter.entityList(stockMessage.getParameters()));

		if (previous != null) {
			previous.setEndDate(validityDate);
			merge(previous);
		}

		List<StockHistoryEntity> nextList = findValidNextList(item, businessUnit, validityDate);

		if (CollectionUtils.isNotEmpty(nextList)) {
			valid.setEndDate(nextList.get(0).getStartDate());
		}

		//if previous start date equals validity date, then the previous end date also becomes equal to validity date. It means that the new stock history interval completely replaces previous interval. Therefore, previous interval should be deleted.
		//It doesn't make sense to keep interval with the same start and end date in the history anyway
		if (previous != null && previous.getStartDate().equals(previous.getEndDate())) {
			remove(previous);
		}

		return persist(valid);
	}

}
