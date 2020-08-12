package skyglass.composer.stock.domain;

public interface StockMovementService {

	Iterable<StockMovement> getAll();

	StockMovement getByUuid(String uuid);

}
