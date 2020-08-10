package skyglass.composer.stock;

public interface StockHistoryService {

	Iterable<StockHistory> getAll();

	StockHistory getByUuid(String uuid);

}
