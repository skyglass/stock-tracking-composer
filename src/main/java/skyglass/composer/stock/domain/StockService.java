package skyglass.composer.stock.domain;

public interface StockService {

	Iterable<Stock> getAll();

	Stock getByUuid(String uuid);

}
