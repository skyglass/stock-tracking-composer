package skyglass.composer.stock.domain.api;

import skyglass.composer.stock.domain.StockMessage;

public interface StockMessageService {

	Iterable<StockMessage> getAll();

	StockMessage getByUuid(String uuid);

}
