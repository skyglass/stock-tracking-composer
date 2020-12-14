package skyglass.composer.stock.entity.repository;

import java.util.Collection;
import java.util.Collections;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.security.entity.repository.ContextRepository;
import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockEntity;

@Repository
@Transactional
public class StockRepository extends AEntityRepository<StockEntity> {

	@Autowired
	private ItemRepository itemBean;

	@Autowired
	private ContextRepository contextRepository;

	private TypedQuery<StockEntity> stockQuery(String queryStr, String whereExtension) {
		if (whereExtension != null) {
			queryStr += " " + whereExtension;
		}

		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		return query;
	}

	private String getDefaultQuery() {
		return "SELECT DISTINCT(st) FROM StockEntity st ";
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
	public Collection<StockEntity> findByContextUuid(String contextUuid) {
		if (StringUtils.isBlank(contextUuid)) {
			return Collections.emptyList();
		}

		contextRepository.findByUuidSecure(contextUuid);

		TypedQuery<StockEntity> query = stockQueryByContextUuid(contextUuid);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	public StockEntity findByItemAndContext(String itemUuid, String contextUuid) {
		TypedQuery<StockEntity> query = stockQueryByItemAndContext(itemUuid, contextUuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	@NotNull
	public StockEntity deactivateStock(String itemUuid, String contextUuid) {
		StockEntity stock = findByItemAndContext(itemUuid, contextUuid);
		if (stock.isActive()) {
			stock.deactivate();
			updateEntity(stock);
		}
		return stock;
	}

	@NotNull
	public StockEntity findOrCreateByItemAndContext(ItemEntity item, ContextEntity context) {
		StockEntity result = findByItemAndContext(item.getUuid(), context.getUuid());
		if (result == null) {
			StockEntity stock = StockEntity.create(item, context);
			result = createEntity(stock);
		}
		return result;
	}

	private TypedQuery<StockEntity> stockQueryByItemAndContext(String itemUuid, String contextUuid) {
		String queryStr = "SELECT st FROM StockEntity st WHERE st.item.uuid = :itemUuid "
				+ "AND st.context.uuid = :contextUuid";
		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		query.setParameter("itemUuid", itemUuid);
		query.setParameter("contextUuid", contextUuid);
		return query;
	}

	private TypedQuery<StockEntity> stockQueryByContextUuid(String contextUuid) {
		String queryStr = "SELECT st FROM StockEntity st WHERE st.context.uuid = :contextUuid";
		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		query.setParameter("contextUuid", contextUuid);
		return query;
	}

	private TypedQuery<StockEntity> stockQueryByItemUuid(String itemUuid) {
		String queryStr = "SELECT st FROM StockEntity st WHERE st.item.uuid = :itemUuid";
		TypedQuery<StockEntity> query = entityBeanUtil.createQuery(queryStr, StockEntity.class);
		query.setParameter("itemUuid", itemUuid);
		return query;
	}

}
