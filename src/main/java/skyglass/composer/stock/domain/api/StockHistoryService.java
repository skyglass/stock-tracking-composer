package skyglass.composer.stock.domain.api;

import skyglass.composer.stock.domain.StockHistory;

public interface StockHistoryService {

	Iterable<StockHistory> getAll();

	StockHistory getByUuid(String uuid);

}
