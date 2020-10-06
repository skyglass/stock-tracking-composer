package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.domain.repository.StockBean;
import skyglass.composer.stock.domain.repository.StockHistoryBean;
import skyglass.composer.stock.domain.repository.StockTransactionBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.exceptions.TransactionRollbackException;

@Repository
@Transactional
public class StockUpdateBean extends AEntityBean<StockEntity> {

	@Autowired
	private StockBean stockBean;

	@Autowired
	private StockHistoryBean stockHistoryBean;

	@Autowired
	private StockTransactionBean stockTransactionBean;

	public void changeStockTo(StockMessage stockMessage) throws TransactionRollbackException {
		StockTransactionEntity transaction = stockTransactionBean.getPendingTransaction(stockMessage);
		if (transaction != null) {
			if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockTo)) {
				changeStock(stockMessage, stockMessage.getTo(), true, false, TransactionType.StockTo);
				stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockTo);
			}
		}
	}

	public void changeStockFrom(StockMessage stockMessage) throws TransactionRollbackException {
		StockTransactionEntity transaction = stockTransactionBean.getPendingTransaction(stockMessage);
		if (transaction != null) {
			if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFrom)) {
				changeStock(stockMessage, stockMessage.getFrom(), false, false, TransactionType.StockFrom);
				stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFrom);
			}
		}
	}

	public void revertStockTo(StockMessage stockMessage) throws TransactionRollbackException {
		StockTransactionEntity transaction = stockTransactionBean.getPendingTransaction(stockMessage);
		if (transaction != null) {
			if (stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockTo)) {
				if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockToRevert)) {
					changeStock(stockMessage, stockMessage.getTo(), false, true, TransactionType.StockToRevert);
					stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockToRevert);
				}
			} else {
				stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockTo);
				if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockToRevert)) {
					stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getTo(), TransactionType.StockToRevert);
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
					stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFromRevert);
				}
			} else {
				stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFrom);
				if (!stockTransactionBean.isCommitted(transaction, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFromRevert)) {
					stockTransactionBean.commitTransactionItem(stockMessage, stockMessage.getItem(), stockMessage.getFrom(), TransactionType.StockFromRevert);
				}
			}
		}
	}

	public void changeStock(StockMessage stockMessage, BusinessUnit businessUnit, boolean increase, boolean isCompensatingTransaction, TransactionType transactionType)
			throws TransactionRollbackException {
		ItemEntity item = entityBeanUtil.find(ItemEntity.class, stockMessage.getItem().getUuid());
		BusinessUnitEntity businessUnitEntity = entityBeanUtil.find(BusinessUnitEntity.class, businessUnit.getUuid());
		StockEntity stock = stockBean.findOrCreateByItemAndBusinessUnit(item, businessUnitEntity);

		double delta = increase ? stockMessage.getAmount() : -stockMessage.getAmount();
		if (!isCompensatingTransaction) {
			validateStock(stock, delta);
		}
		doStockUpdate(stockMessage, stock, item, businessUnitEntity, delta);
	}

	private void doStockUpdate(StockMessage stockMessage, StockEntity stock, ItemEntity item, BusinessUnitEntity businessUnit, double delta) {
		stock.updateAmount(delta);
		entityBeanUtil.merge(stock);
		stockHistoryBean.createHistory(stockMessage, item, businessUnit, delta);
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
