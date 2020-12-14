package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.repository.StockHistoryRepository;
import skyglass.composer.stock.entity.repository.StockRepository;
import skyglass.composer.stock.entity.repository.StockTransactionRepository;
import skyglass.composer.stock.exceptions.TransactionRollbackException;

@Repository
@Transactional
public class StockUpdateRepository extends AEntityRepository<StockEntity> {

	@Autowired
	private StockRepository stockBean;

	@Autowired
	private StockHistoryRepository stockHistoryBean;

	@Autowired
	private StockTransactionRepository stockTransactionBean;

	public void changeStockTo(StockMessage stockMessage) throws TransactionRollbackException {
		StockTransactionEntity transaction = stockTransactionBean.getPendingTransaction(stockMessage);
		if (transaction != null) {
			if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockTo)) {
				changeStock(stockMessage, stockMessage.getTo(), true, false, TransactionType.StockTo);
				stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockTo);
			}
		}
	}

	public void changeStockFrom(StockMessage stockMessage) throws TransactionRollbackException {
		StockTransactionEntity transaction = stockTransactionBean.getPendingTransaction(stockMessage);
		if (transaction != null) {
			if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFrom)) {
				changeStock(stockMessage, stockMessage.getFrom(), false, false, TransactionType.StockFrom);
				stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockFrom);
			}
		}
	}

	public void revertStockTo(StockMessage stockMessage) throws TransactionRollbackException {
		StockTransactionEntity transaction = stockTransactionBean.getPendingTransaction(stockMessage);
		if (transaction != null) {
			if (stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockTo)) {
				if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockToRevert)) {
					changeStock(stockMessage, stockMessage.getTo(), false, true, TransactionType.StockToRevert);
					stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockToRevert);
				}
			} else {
				stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockTo);
				if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockToRevert)) {
					stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockToRevert);
				}
			}
		}
	}

	public void revertStockFrom(StockMessage stockMessage) throws TransactionRollbackException {
		StockTransactionEntity transaction = stockTransactionBean.getPendingTransaction(stockMessage);
		if (transaction != null) {
			if (stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFrom)) {
				if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFromRevert)) {
					changeStock(stockMessage, stockMessage.getFrom(), true, true, TransactionType.StockFromRevert);
					stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockFromRevert);
				}
			} else {
				stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockFrom);
				if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFromRevert)) {
					stockTransactionBean.commitTransactionItem(stockMessage, TransactionType.StockFromRevert);
				}
			}
		}
	}

	public void changeStock(StockMessage stockMessage, Context context, boolean increase, boolean isCompensatingTransaction, TransactionType transactionType)
			throws TransactionRollbackException {
		ItemEntity item = entityBeanUtil.find(ItemEntity.class, stockMessage.getItem().getUuid());
		ContextEntity contextEntity = entityBeanUtil.find(ContextEntity.class, context.getUuid());
		StockEntity stock = stockBean.findOrCreateByItemAndContext(item, contextEntity);

		double delta = increase ? stockMessage.getAmount() : -stockMessage.getAmount();
		if (!isCompensatingTransaction) {
			validateStock(stock, delta);
		}
		doStockUpdate(stockMessage, stock, item, contextEntity, delta);
	}

	private void doStockUpdate(StockMessage stockMessage, StockEntity stock, ItemEntity item, ContextEntity context, double delta) {
		stock.updateAmount(delta);
		entityBeanUtil.merge(stock);
		stockHistoryBean.createHistory(stockMessage, item, context, delta);
	}

	private void validateStock(StockEntity stock, double delta) throws TransactionRollbackException {
		if (!isStockValid(stock, delta)) {
			throw new TransactionRollbackException("Stock Update Error");
		}
	}

	private boolean isStockValid(StockEntity stock, double delta) {
		return stock.isActive() && (Stock.isStockCenter(stock) || stock.getAmount() + delta >= 0);
	}

}
