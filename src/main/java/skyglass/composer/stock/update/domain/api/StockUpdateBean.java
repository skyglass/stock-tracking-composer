package skyglass.composer.stock.update.domain.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.StockMessage;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockEntity;

@Repository
@Transactional
public class StockUpdateBean extends AEntityBean<StockEntity> {

	@Autowired
	private StockBean stockBean;

	public void changeStockTo(StockUpdate stockUpdate) {
		StockMessage stockMessage = entityBeanUtil.find(StockMessage.class, stockUpdate.getStockMessageUuid());
		BusinessUnitEntity businessUnit = entityBeanUtil.find(BusinessUnitEntity.class, stockUpdate.getToUuid());
		ItemEntity item = entityBeanUtil.find(ItemEntity.class, stockUpdate.getItemUuid());
		updateStockTo(stockUpdate, stockMessage, item, businessUnit);
	}

	public void changeStockFrom(StockUpdate stockUpdate) {
		StockMessage stockMovement = entityBeanUtil.find(StockMessage.class, stockUpdate.getStockMessageUuid());
		BusinessUnitEntity businessUnit = entityBeanUtil.find(BusinessUnitEntity.class, stockUpdate.getFromUuid());
		ItemEntity item = entityBeanUtil.find(ItemEntity.class, stockUpdate.getItemUuid());
		updateStockFrom(stockUpdate, stockMovement, item, businessUnit);
	}

	private void updateStockTo(StockUpdate stockUpdate, StockMessage stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		StockEntity stockTo = stockBean.findOrCreateByItemAndBusinessUnit(item, businessUnit);
		if (stockUpdate.shouldUpdateStock()) {
			doStockToUpdate(stockUpdate, stockTo, stockMessage, item, businessUnit);
		}
	}

	private void updateStockFrom(StockUpdate stockUpdate, StockMessage stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		StockEntity stockFrom = stockBean.findOrCreateByItemAndBusinessUnit(item, businessUnit);
		if (stockUpdate.shouldUpdateStock()) {
			doStockFromUpdate(stockUpdate, stockFrom, stockMessage, item, businessUnit);
		}
	}

	private void doStockFromUpdate(StockUpdate stockUpdate, StockEntity stockFrom, StockMessage stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		StockEntity result = stockUpdate.setStockAmount(stockFrom, stockFrom.getAmount() - stockMessage.getAmount());
		entityBeanUtil.merge(result);
	}

	private void doStockToUpdate(StockUpdate stockUpdate, StockEntity stockTo, StockMessage stockMessage, ItemEntity item, BusinessUnitEntity businessUnit) {
		StockEntity result = stockUpdate.setStockAmount(stockTo, stockTo.getAmount() + stockMessage.getAmount());
		entityBeanUtil.merge(result);
	}

}
