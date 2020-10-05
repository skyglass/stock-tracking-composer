package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.model.Stock;

public interface StockService {

	Iterable<Stock> getAll();

	Stock getByUuid(String uuid);

	Stock findByItemAndBusinessUnit(String itemUuid, String businessUnitUuid);

	Stock deactivate(String itemUuid, String businessUnitUuid);

}
