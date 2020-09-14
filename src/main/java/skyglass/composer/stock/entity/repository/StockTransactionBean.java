package skyglass.composer.stock.entity.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockTransactionEntity;

public class StockTransactionBean extends AEntityBean<StockTransactionEntity> {
	
	private ItemBean itemBean;
	
	private BusinessUnitBean businessUnitBean;
	
	private StockMessageBean stockMessageBean;
	
	public StockTransactionEntity findByMessage(String messageUuid) {
		stockMessageBean.findByUuidSecure(messageUuid);
		String queryStr = "SELECT st FROM StockTransactionEntity st WHERE st.message.uuid = :messageUuid ";
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		query.setParameter("messageUuid", messageUuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	public List<StockTransactionEntity> findByItemAndBusinessUnit(String itemUuid, String businessUnitUuid) {
		itemBean.findByUuidSecure(itemUuid);
		businessUnitBean.findByUuidSecure(businessUnitUuid);
		String queryStr = "SELECT st FROM StockTransactionEntity st WHERE st.message.item.uuid = :itemUuid "
				+ "AND st.message.businessUnit.uuid = :businessUnitUuid";
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		query.setParameter("itemUuid", itemUuid);
		query.setParameter("businessUnitUuid", businessUnitUuid);
		return EntityUtil.getListResultSafely(query);
	}

}
