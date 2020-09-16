package skyglass.composer.stock.domain.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.model.TransactionItem;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;

public class TransactionItemBean extends AEntityBean<TransactionItemEntity> {
	
	@Autowired
	private StockTransactionBean stockTransactionBean;

	public List<TransactionItemEntity> findByTransaction(String transactionUuid) {
		stockTransactionBean.findByUuidSecure(transactionUuid);
		String queryStr = "SELECT st FROM TransactionItemEntity ti WHERE ti.transaction.uuid = :transactionUuid";
		TypedQuery<TransactionItemEntity> query = entityBeanUtil.createQuery(queryStr, TransactionItemEntity.class);
		query.setParameter("transactionUuid", transactionUuid);
		return EntityUtil.getListResultSafely(query);
	}
	
	public List<TransactionItemEntity> findByKey(String key) {
		String queryStr = "SELECT st FROM TransactionItemEntity ti WHERE ti.key = :key";
		TypedQuery<TransactionItemEntity> query = entityBeanUtil.createQuery(queryStr, TransactionItemEntity.class);
		query.setParameter("key", key);
		return EntityUtil.getListResultSafely(query);
	}
	
	public TransactionItemEntity findByTransactionType(StockTransactionEntity transaction, TransactionType transactionType) {
		String queryStr = "SELECT st FROM TransactionItemEntity ti WHERE ti.transaction.uuid = :transactionUuid AND ti.transactionType = :transactionType";
		TypedQuery<TransactionItemEntity> query = entityBeanUtil.createQuery(queryStr, TransactionItemEntity.class);
		query.setParameter("transactionUuid", transaction.getUuid());
		query.setParameter("transactionType", transactionType);
		return EntityUtil.getSingleResultSafely(query);
	}
	
	public TransactionItemEntity create(StockTransactionEntity transaction, String key, TransactionType transactionType) {
		return create(TransactionItem.create(transaction, key, transactionType));
	}

}
