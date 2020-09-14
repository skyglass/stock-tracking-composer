package skyglass.composer.stock.entity.service;

import java.util.List;

import skyglass.composer.stock.domain.model.StockTransaction;

public interface StockTransactionService {
	
	StockTransaction getByUuid(String uuid);

	StockTransaction findByMessage(String messageUuid);
	
	List<StockTransaction> findByItemAndBusinessUnit(String itemUuid, String businessUnitUuid);

}
