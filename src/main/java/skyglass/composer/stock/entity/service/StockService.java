package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;

public interface StockService {

	Iterable<Stock> getAll();

	Stock getByUuid(String uuid);

	Stock findByItemAndBusinessUnit(Item item, BusinessUnit businessUnit);

}
