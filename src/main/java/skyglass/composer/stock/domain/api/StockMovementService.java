package skyglass.composer.stock.domain.api;

import skyglass.composer.stock.domain.StockMovement;

public interface StockMovementService {

	Iterable<StockMovement> getAll();

	StockMovement getByUuid(String uuid);

}
