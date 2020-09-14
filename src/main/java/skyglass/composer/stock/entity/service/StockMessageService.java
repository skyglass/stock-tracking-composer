package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.model.StockMessage;

public interface StockMessageService {

	Iterable<StockMessage> getAll();

	StockMessage getByUuid(String uuid);

}
