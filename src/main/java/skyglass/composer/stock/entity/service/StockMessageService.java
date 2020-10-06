package skyglass.composer.stock.entity.service;

import java.util.Collection;

import skyglass.composer.stock.domain.model.StockMessage;

public interface StockMessageService {

	Collection<StockMessage> getAll();

	StockMessage getByUuid(String uuid);

}
