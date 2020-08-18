package skyglass.composer.stock.domain.api;

import skyglass.composer.stock.domain.Stock;

public interface StockService {

	Iterable<Stock> getAll();

	Stock getByUuid(String uuid);

}
