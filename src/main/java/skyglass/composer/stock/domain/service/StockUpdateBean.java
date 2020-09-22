package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.repository.StockBean;
import skyglass.composer.stock.domain.repository.StockHistoryBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockEntity;
import skyglass.composer.stock.exceptions.TransactionRollbackException;

@Repository
@Transactional
public class StockUpdateBean extends AEntityBean<StockEntity> {

	@Autowired
	private StockBean stockBean;

	@Autowired
	private StockHistoryBean stockHistoryBean;

	public void changeStockTo(StockMessage stockMessage) throws TransactionRollbackException {
		changeStock(stockMessage, stockMessage.getTo(), true, false);
	}

	public void changeStockFrom(StockMessage stockMessage) throws TransactionRollbackException {
		changeStock(stockMessage, stockMessage.getFrom(), false, false);
	}

	public void revertStockTo(StockMessage stockMessage) throws TransactionRollbackException {
		changeStock(stockMessage, stockMessage.getTo(), false, true);
	}

	public void revertStockFrom(StockMessage stockMessage) throws TransactionRollbackException {
		changeStock(stockMessage, stockMessage.getFrom(), true, true);
	}

	public void changeStock(StockMessage stockMessage, BusinessUnit businessUnit, boolean increase, boolean isCompensatingTransaction) throws TransactionRollbackException {
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
		return stock.isActive() && stock.getAmount() + delta >= 0;
	}

}
