package skyglass.composer.stock;

public interface StockMovementService {

	Iterable<StockMovement> getAll();

	StockMovement getByUuid(String uuid);

}
