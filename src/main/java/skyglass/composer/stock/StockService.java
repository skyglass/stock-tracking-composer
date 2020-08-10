package skyglass.composer.stock;

public interface StockService {

	Iterable<Stock> getAll();

	Stock getByUuid(String uuid);

}
