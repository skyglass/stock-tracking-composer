package skyglass.composer.stock.persistence.service;

import skyglass.composer.stock.domain.StockMessage;

public interface StockMessageService {

	Iterable<StockMessage> getAll();

	StockMessage getByUuid(String uuid);

}
