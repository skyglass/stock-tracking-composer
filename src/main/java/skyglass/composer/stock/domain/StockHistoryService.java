package skyglass.composer.stock.domain;

public interface StockHistoryService {

	Iterable<StockHistory> getAll();

	StockHistory getByUuid(String uuid);

}
