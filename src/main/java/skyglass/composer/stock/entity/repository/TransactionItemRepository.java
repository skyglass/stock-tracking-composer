package skyglass.composer.stock.entity.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.domain.factory.TransactionItemFactory;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class TransactionItemRepository extends AEntityRepository<TransactionItemEntity> {

	@Autowired
	private TransactionItemFactory transactionItemFactory;

	public List<TransactionItemEntity> findByTransaction(StockTransactionEntity transaction) {
		String queryStr = "SELECT ti FROM TransactionItemEntity ti WHERE ti.transaction.uuid = :transactionUuid";
		TypedQuery<TransactionItemEntity> query = entityBeanUtil.createQuery(queryStr, TransactionItemEntity.class);
		query.setParameter("transactionUuid", transaction.getUuid());
		return EntityUtil.getListResultSafely(query);
	}

	public List<TransactionItemEntity> findByKey(String key) {
		String queryStr = "SELECT ti FROM TransactionItemEntity ti WHERE ti.key = :key";
		TypedQuery<TransactionItemEntity> query = entityBeanUtil.createQuery(queryStr, TransactionItemEntity.class);
		query.setParameter("key", key);
		return EntityUtil.getListResultSafely(query);
	}

	public TransactionItemEntity findByTransactionType(StockTransactionEntity transaction, TransactionType transactionType) {
		String queryStr = "SELECT ti FROM TransactionItemEntity ti WHERE ti.transaction.uuid = :transactionUuid AND ti.transactionType = :transactionType";
		TypedQuery<TransactionItemEntity> query = entityBeanUtil.createQuery(queryStr, TransactionItemEntity.class);
		query.setParameter("transactionUuid", transaction.getUuid());
		query.setParameter("transactionType", transactionType);
		return EntityUtil.getSingleResultSafely(query);
	}

	public TransactionItemEntity create(StockTransactionEntity transaction, String key, TransactionType transactionType) {
		return createEntity(transactionItemFactory.entity(transaction, key, transactionType));
	}

}
