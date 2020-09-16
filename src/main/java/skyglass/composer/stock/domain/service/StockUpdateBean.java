package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.domain.repository.StockBean;
import skyglass.composer.stock.domain.repository.StockHistoryBean;
import skyglass.composer.stock.domain.repository.StockTransactionBean;
import skyglass.composer.stock.domain.repository.TransactionItemBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;

@Repository
@Transactional
public class StockUpdateBean extends AEntityBean<StockEntity> {

	@Autowired
	private StockBean stockBean;

	@Autowired
	private StockHistoryBean stockHistoryBean;
	
	@Autowired
	private StockTransactionBean stockTransactionBean;
	
	@Autowired
	private TransactionItemBean transactionItemBean;
	
	public void revertStockTo(StockUpdate stockUpdate) {
		changeStockFrom(stockUpdate);
	}

	public void revertStockFrom(StockUpdate stockUpdate) {
		changeStockTo(stockUpdate);
	}
	
	public void changeStockTo(StockUpdate stockUpdate) {
		StockMessageEntity stockMessage = entityBeanUtil.find(StockMessageEntity.class, stockUpdate.getStockMessageUuid());
		ItemEntity item = entityBeanUtil.find(ItemEntity.class, stockUpdate.getItemUuid());
		BusinessUnitEntity businessUnit = entityBeanUtil.find(BusinessUnitEntity.class, stockUpdate.getToUuid());
		updateStockTo(stockUpdate, stockMessage, item, businessUnit);
	}

	public void changeStockFrom(StockUpdate stockUpdate) {
		StockMessageEntity stockMessage = entityBeanUtil.find(StockMessageEntity.class, stockUpdate.getStockMessageUuid());
		ItemEntity item = entityBeanUtil.find(ItemEntity.class, stockUpdate.getItemUuid());
		BusinessUnitEntity businessUnit = entityBeanUtil.find(BusinessUnitEntity.class, stockUpdate.getFromUuid());
		updateStockFrom(stockUpdate, stockMessage, item, businessUnit);
	}

	private void updateStockTo(StockUpdate stockUpdate, StockMessageEntity stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		StockEntity stockTo = stockBean.findOrCreateByItemAndBusinessUnit(item, businessUnit);
		if (stockUpdate.shouldUpdateStock()) {
			doStockToUpdate(stockUpdate, stockTo, stockMessage, item, businessUnit);
		}
	}

	private void updateStockFrom(StockUpdate stockUpdate, StockMessageEntity stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		StockEntity stockFrom = stockBean.findOrCreateByItemAndBusinessUnit(item, businessUnit);
		if (stockUpdate.shouldUpdateStock()) {
			doStockFromUpdate(stockUpdate, stockFrom, stockMessage, item, businessUnit);
		}
	}

	private void doStockFromUpdate(StockUpdate stockUpdate, StockEntity stockFrom, StockMessageEntity stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		stockFrom.updateAmount(-stockMessage.getAmount());
		entityBeanUtil.merge(stockFrom);
		stockHistoryBean.createHistory(stockFrom, stockMessage, item, businessUnit, true);
	}

	private void doStockToUpdate(StockUpdate stockUpdate, StockEntity stockTo, StockMessageEntity stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		stockTo.updateAmount(stockMessage.getAmount());
		entityBeanUtil.merge(stockTo);
		stockHistoryBean.createHistory(stockTo, stockMessage, item, businessUnit, false);
	}
	
	private void validateStock(StockEntity stock, StockMessageEntity stockMessage, TransactionType transactionType) {
		StockTransactionEntity transaction = stockTransactionBean.findByMessage(stockMessage.getUuid());
		TransactionItemEntity transactionItem = transactionItemBean.findByTransactionType(transaction, transactionType);
		boolean valid = isStockValid(stock);
		boolean canceled = transaction.isCanceled();
		if (valid && !canceled) {
			
		}
		if (!valid && canceled) {
			
		}
		if (valid && canceled) {
			
		}		
		if (!valid && !canceled) {
			
		}
	}
	
	private boolean isStockValid(StockEntity stock) {
		return stock.isActive() && stock.getAmount() >= 0;
	}

}
