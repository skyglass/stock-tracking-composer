package skyglass.composer.stock.update.domain.api;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.EntityUtil;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockEntity;

@Repository
@Transactional
public class StockBean extends AEntityBean<StockEntity> {

	@Autowired
	private ItemBean itemBean;

	@Autowired
	private BusinessUnitBean businessUnitBean;

	private TypedQuery<StockEntity> stockQuery(String queryStr, String whereExtension) {
		if (whereExtension != null) {
			queryStr += " " + whereExtension;
		}

		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		return query;
	}

	private String getDefaultQuery() {
		return "SELECT DISTINCT(st) FROM Stock st ";
	}

	@Override
	public StockEntity findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}
		String whereExtension = "AND st.uuid = :uuid";
		TypedQuery<StockEntity> query = stockQuery(getDefaultQuery(), whereExtension);
		query.setParameter("uuid", uuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	public Collection<StockEntity> findAll() {
		TypedQuery<StockEntity> query = stockQuery(getDefaultQuery(), null);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	public Collection<StockEntity> findByItem(String itemUuid) {
		if (StringUtils.isBlank(itemUuid)) {
			return Collections.emptyList();
		}

		itemBean.findByUuidSecure(itemUuid);

		TypedQuery<StockEntity> query = stockQueryByItemUuid(itemUuid);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	public Collection<StockEntity> findByBusinessUnitUuid(String businessUnitUuid) {
		if (StringUtils.isBlank(businessUnitUuid)) {
			return Collections.emptyList();
		}

		businessUnitBean.findByUuidSecure(businessUnitUuid);

		TypedQuery<StockEntity> query = stockQueryByBusinessUnitUuid(businessUnitUuid);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	public StockEntity findByItemUuidAndBusinessUnitUuid(String itemUuid, String businessUnitUuid) {
		if (StringUtils.isAnyBlank(itemUuid, businessUnitUuid)) {
			return null;
		}

		itemBean.findByUuidSecure(itemUuid);
		businessUnitBean.findByUuidSecure(businessUnitUuid);

		TypedQuery<StockEntity> query = stockQueryByItemUuidAndBusinessUnitUuid(itemUuid, businessUnitUuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	@NotNull
	public StockEntity findOrCreateByItemAndBusinessUnit(ItemEntity item, BusinessUnitEntity businessUnit) {
		StockEntity result = findByItemUuidAndBusinessUnitUuid(item.getUuid(), businessUnit.getUuid());
		if (result == null) {
			StockEntity stock = new StockEntity(null, item, businessUnit, 0D);
			result = entityBeanUtil.persist(stock);
		}
		return result;
	}

	private TypedQuery<StockEntity> stockQueryByItemUuidAndBusinessUnitUuid(String itemUuid, String businessUnitUuid) {
		String queryStr = "SELECT st FROM Stock st WHERE st.item.uuid = :itemUuid "
				+ "AND st.businessUnit.uuid = :businessUnitUuid";
		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		query.setParameter("itemUuid", itemUuid);
		query.setParameter("businessUnitUuid", businessUnitUuid);
		return query;
	}

	private TypedQuery<StockEntity> stockQueryByBusinessUnitUuid(String businessUnitUuid) {
		String queryStr = "SELECT st FROM StockEntity st WHERE st.businessUnit.uuid = :businessUnitUuid";
		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		query.setParameter("businessUnitUuid", businessUnitUuid);
		return query;
	}

	private TypedQuery<StockEntity> stockQueryByItemUuid(String itemUuid) {
		String queryStr = "SELECT st FROM StockEntity st WHERE st.item.uuid = :itemUuid";
		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		query.setParameter("itemUuid", itemUuid);
		return query;
	}

}
