package skyglass.composer.stock.persistence.service;

import skyglass.composer.stock.domain.StockHistory;

public interface StockHistoryService {

	Iterable<StockHistory> getAll();

	StockHistory getByUuid(String uuid);

}
