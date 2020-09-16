package skyglass.composer.stock.domain.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.model.StockTransaction;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class StockTransactionBean extends AEntityBean<StockTransactionEntity> {
	
	@Autowired
	private ItemBean itemBean;
	
	@Autowired
	private BusinessUnitBean businessUnitBean;
	
	@Autowired
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
	
	public StockTransactionEntity create(StockMessageEntity stockMessage) {
		return create(StockTransaction.create(stockMessage));
	}

}
