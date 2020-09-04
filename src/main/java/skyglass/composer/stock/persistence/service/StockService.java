package skyglass.composer.stock.persistence.service;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.Stock;

public interface StockService {

	Iterable<Stock> getAll();

	Stock getByUuid(String uuid);

	Stock findByItemAndBusinessUnit(Item item, BusinessUnit businessUnit);

}
