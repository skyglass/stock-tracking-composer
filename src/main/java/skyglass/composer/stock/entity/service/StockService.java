package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.model.Stock;

public interface StockService {

	Iterable<Stock> getAll();

	Stock getByUuid(String uuid);

	Stock findByItemAndContext(String itemUuid, String contextUuid);

	Stock deactivate(String itemUuid, String contextUuid);

}
