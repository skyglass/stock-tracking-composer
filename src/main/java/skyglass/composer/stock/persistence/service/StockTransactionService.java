package skyglass.composer.stock.persistence.service;

import skyglass.composer.stock.domain.Stock;
import skyglass.composer.stock.domain.StockMessage;

public interface StockTransactionService {
	
	Stock getByUuid(String uuid);

	Stock findByMessage(StockMessage message);

}
