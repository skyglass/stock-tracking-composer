package skyglass.composer.stock.domain.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.model.StockTransaction;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;
import skyglass.composer.stock.exceptions.InvalidTransactionStateException;

@Repository
@Transactional
public class StockTransactionBean extends AEntityBean<StockTransactionEntity> {

	@Autowired
	private TransactionItemBean transactionItemBean;

	public void deleteCommittedTransactions() {
		deleteCommittedTransactions(null, null);
	}

	public void deleteCommittedTransactions(Item item, BusinessUnit businessUnit) {
		String queryStr = "DELETE FROM TransactionItemEntity ti WHERE ti.uuid IN (SELECT ti.uuid FROM TransactionItemEntity ti JOIN ti.transaction tr JOIN tr.message m "
				+ "WHERE tr.pending = :pending"
				+ (item != null ? " AND m.item.uuid = :itemUuid AND (m.from.uuid = :businessUnitUuid OR m.to.uuid = :businessUnitUuid))" : ")");
		Query itemQuery = entityBeanUtil.createQuery(queryStr);
		if (item != null) {
			itemQuery.setParameter("itemUuid", item.getUuid());
			itemQuery.setParameter("businessUnitUuid", businessUnit.getUuid());
		}
		itemQuery.setParameter("pending", false);
		itemQuery.executeUpdate();

		queryStr = "DELETE FROM StockTransactionEntity tr WHERE tr.uuid IN (SELECT tr.uuid FROM StockTransactionEntity tr JOIN tr.message m "
				+ "WHERE tr.pending = :pending"
				+ (item != null ? " AND m.item.uuid = :itemUuid AND (m.from.uuid = :businessUnitUuid OR m.to.uuid = :businessUnitUuid))" : ")");
		Query query = entityBeanUtil.createQuery(queryStr);
		if (item != null) {
			query.setParameter("itemUuid", item.getUuid());
			query.setParameter("businessUnitUuid", businessUnit.getUuid());
		}
		query.setParameter("pending", false);
		query.executeUpdate();
	}

	public StockTransactionEntity findByMessage(StockMessage stockMessage) {
		String queryStr = "SELECT st FROM StockTransactionEntity st WHERE st.message.uuid = :messageUuid";
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		query.setParameter("messageUuid", stockMessage.getUuid());
		return EntityUtil.getSingleResultSafely(query);
	}

	public List<StockMessage> findPendingMessages() {
		return findPendingMessages(null, null);
	}

	public List<StockMessage> findPendingMessages(Item item, BusinessUnit businessUnit) {
		return findPendingTransactions(item, businessUnit).stream().map(e -> StockMessage.mapEntity(e.getMessage())).collect(Collectors.toList());
	}

	private List<StockTransactionEntity> findPendingTransactions(Item item, BusinessUnit businessUnit) {
		String queryStr = "SELECT st FROM StockTransactionEntity st WHERE st.pending = :pending"
				+ (item != null ? " AND st.message.item.uuid = :itemUuid AND (st.message.from.uuid = :businessUnitUuid OR st.message.to.uuid = :businessUnitUuid)" : "");
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		if (item != null) {
			query.setParameter("itemUuid", item.getUuid());
			query.setParameter("businessUnitUuid", businessUnit.getUuid());
		}
		query.setParameter("pending", true);
		return EntityUtil.getListResultSafely(query);
	}

	public StockTransactionEntity create(StockMessageEntity stockMessage) {
		return create(StockTransaction.create(stockMessage));
	}

	public StockTransactionEntity getPendingTransaction(StockMessage stockMessage) {
		StockTransactionEntity transaction = findByMessage(stockMessage);
		if (transaction != null && transaction.isPending()) {
			return transaction;
		}
		return null;
	}

	public boolean isCommitted(StockTransactionEntity transaction, Item item, BusinessUnit businessUnit, TransactionType transactionType) {
		TransactionItemEntity transactionItem = transactionItemBean.findByTransactionType(transaction, transactionType);
		if (transactionItem == null) {
			transactionItem = transactionItemBean.create(transaction, Stock.key(item.getUuid(), businessUnit.getUuid()), transactionType);
		}
		return !transactionItem.isPending();
	}

	public void commitTransactionItem(StockMessage stockMessage, Item item, BusinessUnit businessUnit, TransactionType transactionType) {
		StockTransactionEntity transaction = findByMessage(stockMessage);
		TransactionItemEntity transactionItem = transactionItemBean.findByTransactionType(transaction, transactionType);
		if (transactionItem == null) {
			throw new InvalidTransactionStateException("Programming Error during Transaction Item Commit. Please, fix the code!");
		}
		if (transactionItem.isPending()) {
			transactionItem.setPending(false);
			entityBeanUtil.merge(transactionItem);
		} else {
			throw new InvalidTransactionStateException("Programming Error during Transaction Item Commit. Please, fix the code!");
		}
	}

	public void commitTransaction(StockMessage stockMessage) {
		assertNoPendingTransactionItems(stockMessage);
		StockTransactionEntity transaction = findByMessage(stockMessage);
		if (transaction != null) {
			transaction.setPending(false);
			entityBeanUtil.merge(transaction);
		}
	}

	public int getPendingTransactionsCount() {
		String queryStr = "SELECT COUNT(st.uuid) FROM StockTransactionEntity st WHERE st.pending = :pending";
		TypedQuery<Long> query = entityBeanUtil.createQuery(queryStr, Long.class);
		query.setParameter("pending", true);
		Long result = EntityUtil.getSingleResultSafely(query);
		if (result == null || result == 0) {
			assertNoPendingTransactionItems();
		}
		return result == null ? 0 : result.intValue();
	}

	public void assertNoPendingTransactionItems() {
		assertNoPendingTransactionItems(null);
	}

	private void assertNoPendingTransactionItems(StockMessage stockMessage) {
		String queryStr = "SELECT COUNT(ti.uuid) FROM TransactionItemEntity ti JOIN ti.transaction t WHERE ti.pending = :pending"
				+ (stockMessage != null ? " AND t.message.uuid = :messageUuid" : "");
		TypedQuery<Long> query = entityBeanUtil.createQuery(queryStr, Long.class);
		if (stockMessage != null) {
			query.setParameter("messageUuid", stockMessage.getUuid());
		}
		query.setParameter("pending", true);
		Long result = EntityUtil.getSingleResultSafely(query);
		if (result != null && result > 0) {
			throw new InvalidTransactionStateException("Programming Error during Transaction Item Commit. Please, fix the code!");
		}
	}

}
