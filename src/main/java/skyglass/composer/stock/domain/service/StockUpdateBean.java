package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.repository.StockBean;
import skyglass.composer.stock.entity.repository.StockHistoryBean;

@Repository
@Transactional
public class StockUpdateBean extends AEntityBean<StockEntity> {

	@Autowired
	private StockBean stockBean;

	@Autowired
	private StockHistoryBean stockHistoryBean;

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

}
