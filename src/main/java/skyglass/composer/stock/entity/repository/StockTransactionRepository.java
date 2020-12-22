package skyglass.composer.stock.entity.repository;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.security.domain.model.Context;
import skyglass.composer.stock.domain.factory.StockMessageFactory;
import skyglass.composer.stock.domain.factory.StockTransactionFactory;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;
import skyglass.composer.stock.exceptions.InvalidTransactionStateException;

@Repository
@Transactional
public class StockTransactionRepository extends AEntityRepository<StockTransactionEntity> {

	@Autowired
	private TransactionItemRepository transactionItemRepository;

	@Autowired
	private StockMessageFactory stockMessageFactory;

	@Autowired
	private StockTransactionFactory stockTransactionFactory;

	public void deleteCommittedTransactions() {
		deleteCommittedTransactions(null, null);
	}

	public void deleteCommittedTransactions(Item item, Context context) {
		String queryStr = "DELETE FROM TransactionItemEntity ti WHERE ti.uuid IN (SELECT ti.uuid FROM TransactionItemEntity ti JOIN ti.transaction tr JOIN tr.message m "
				+ "WHERE tr.pending = :pending"
				+ (item != null ? " AND m.item.uuid = :itemUuid AND (m.from.uuid = :contextUuid OR m.to.uuid = :contextUuid))" : ")");
		Query itemQuery = entityBeanUtil.createQuery(queryStr);
		if (item != null) {
			itemQuery.setParameter("itemUuid", item.getUuid());
			itemQuery.setParameter("contextUuid", context.getUuid());
		}
		itemQuery.setParameter("pending", false);
		itemQuery.executeUpdate();

		queryStr = "DELETE FROM StockTransactionEntity tr WHERE tr.uuid IN (SELECT tr.uuid FROM StockTransactionEntity tr JOIN tr.message m "
				+ "WHERE tr.pending = :pending"
				+ (item != null ? " AND m.item.uuid = :itemUuid AND (m.from.uuid = :contextUuid OR m.to.uuid = :contextUuid))" : ")");
		Query query = entityBeanUtil.createQuery(queryStr);
		if (item != null) {
			query.setParameter("itemUuid", item.getUuid());
			query.setParameter("contextUuid", context.getUuid());
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

	public List<StockMessage> findPendingMessages(Item item, Context context) {
		return findPendingTransactions(item, context).stream()
				.map(e -> stockMessageFactory.object(e.getMessage()))
				.collect(Collectors.toList());
	}

	private List<StockTransactionEntity> findPendingTransactions(Item item, Context context) {
		String queryStr = "SELECT st FROM StockTransactionEntity st WHERE st.pending = :pending"
				+ (item != null ? " AND st.message.item.uuid = :itemUuid AND (st.message.from.uuid = :contextUuid OR st.message.to.uuid = :contextUuid)" : "");
		TypedQuery<StockTransactionEntity> query = entityBeanUtil.createQuery(queryStr, StockTransactionEntity.class);
		if (item != null) {
			query.setParameter("itemUuid", item.getUuid());
			query.setParameter("contextUuid", context.getUuid());
		}
		query.setParameter("pending", true);
		return EntityUtil.getListResultSafely(query);
	}

	public StockTransactionEntity create(StockMessageEntity stockMessage) {
		return createEntity(stockTransactionFactory.createEntity(stockMessage));
	}

	public StockTransactionEntity getPendingTransaction(StockMessage stockMessage) {
		StockTransactionEntity transaction = findByMessage(stockMessage);
		if (transaction != null && transaction.isPending()) {
			return transaction;
		}
		return null;
	}

	public boolean isCommitted(StockTransactionEntity transaction, Item item, Context context, TransactionType transactionType) {
		TransactionItemEntity transactionItem = transactionItemRepository.findByTransactionType(transaction, transactionType);
		if (transactionItem == null) {
			transactionItem = transactionItemRepository.create(transaction, Stock.key(item.getUuid(), context.getUuid()), transactionType);
		}
		return !transactionItem.isPending();
	}

	public void commitTransactionItem(StockMessage stockMessage, TransactionType transactionType) {
		StockTransactionEntity transaction = findByMessage(stockMessage);
		TransactionItemEntity transactionItem = transactionItemRepository.findByTransactionType(transaction, transactionType);
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
