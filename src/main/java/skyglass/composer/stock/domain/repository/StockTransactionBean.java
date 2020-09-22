package skyglass.composer.stock.domain.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.model.StockTransaction;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;

@Repository
@Transactional(propagation = Propagation.MANDATORY)
public class StockTransactionBean extends AEntityBean<StockTransactionEntity> {

	@Autowired
	private ItemBean itemBean;

	@Autowired
	private BusinessUnitBean businessUnitBean;

	@Autowired
	private StockMessageBean stockMessageBean;

	@Autowired
	private TransactionItemBean transactionItemBean;

	public void deleteCommittedTransactions(Item item, BusinessUnit businessUnit) {
		String queryStr = "DELETE FROM TransactionItemEntity ti WHERE ti.uuid IN (SELECT ti.uuid FROM TransactionItemEntity ti JOIN ti.transaction tr WHERE tr.pending = :pending AND tr.item.uuid = :itemUuid AND tr.businessUnit.uuid = :businessUnitUuid)";
		TypedQuery<TransactionItemEntity> itemQuery = entityBeanUtil.createQuery(queryStr, TransactionItemEntity.class);
		itemQuery.setParameter("itemUuid", item.getUuid());
		itemQuery.setParameter("businessUnitUuid", businessUnit.getUuid());
		itemQuery.setParameter("pending", false);
		itemQuery.executeUpdate();

		queryStr = "DELETE FROM StockTransactionEntity tr WHERE tr.pending = :pending AND tr.item.uuid = :itemUuid AND tr.businessUnit.uuid = :businessUnitUuid)";
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("businessUnitUuid", businessUnit.getUuid());
		query.setParameter("pending", false);
		query.executeUpdate();
	}

	public StockTransactionEntity findByMessage(String messageUuid) {
		stockMessageBean.findByUuidSecure(messageUuid);
		String queryStr = "SELECT st FROM StockTransactionEntity st WHERE st.message.uuid = :messageUuid ";
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		query.setParameter("messageUuid", messageUuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	public List<StockMessage> findPendingMessages(Item item, BusinessUnit businessUnit) {
		return findPendingTransactions(item, businessUnit).stream().map(e -> StockMessage.mapEntity(e.getMessage())).collect(Collectors.toList());
	}

	private List<StockTransactionEntity> findPendingTransactions(Item item, BusinessUnit businessUnit) {
		itemBean.findByUuidSecure(item.getUuid());
		businessUnitBean.findByUuidSecure(businessUnit.getUuid());
		String queryStr = "SELECT st FROM StockTransactionEntity st WHERE st.message.item.uuid = :itemUuid "
				+ "AND st.message.businessUnit.uuid = :businessUnitUuid AND st.pending = :pending";
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		query.setParameter("itemUuid", item.getUuid());
		query.setParameter("businessUnitUuid", businessUnit.getUuid());
		query.setParameter("pending", true);
		return EntityUtil.getListResultSafely(query);
	}

	public StockTransactionEntity create(StockMessageEntity stockMessage) {
		return create(StockTransaction.create(stockMessage));
	}

	public boolean isCommitted(StockMessage stockMessage, TransactionType transactionType) {
		StockTransactionEntity transaction = findByMessage(stockMessage.getUuid());
		TransactionItemEntity transactionItem = transactionItemBean.findByTransactionType(transaction, transactionType);
		return !transactionItem.isPending();
	}

	public void commitTransactionItem(StockMessage stockMessage, TransactionType transactionType) {
		StockTransactionEntity transaction = findByMessage(stockMessage.getUuid());
		TransactionItemEntity transactionItem = transactionItemBean.findByTransactionType(transaction, transactionType);
		if (transactionItem.isPending()) {
			transactionItem.setPending(false);
			entityBeanUtil.merge(transactionItem);
		}
	}

	public void commitTransaction(StockMessage stockMessage) {
		StockTransactionEntity transaction = findByMessage(stockMessage.getUuid());
		if (transaction.isPending()) {
			transaction.setPending(false);
			entityBeanUtil.merge(transaction);
		}
	}

}
