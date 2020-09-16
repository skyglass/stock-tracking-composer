package skyglass.composer.stock.entity.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.domain.model.StockTransaction;
import skyglass.composer.stock.domain.repository.StockTransactionBean;

public class StockTransactionServiceImpl implements StockTransactionService {
	
	@Autowired
	private StockTransactionBean stockTransactionBean;

	@Override
	public StockTransaction getByUuid(String uuid) {
		return StockTransaction.mapEntity(stockTransactionBean.findByUuidSecure(uuid));
	}

	@Override
	public StockTransaction findByMessage(String messageUuid) {
		return StockTransaction.mapEntity(stockTransactionBean.findByMessage(messageUuid));
	}

	@Override
	public List<StockTransaction> findByItemAndBusinessUnit(String itemUuid, String businessUnitUuid) {
		return StockTransaction.mapEntityList(stockTransactionBean.findByItemAndBusinessUnit(itemUuid, businessUnitUuid));
	}

}
