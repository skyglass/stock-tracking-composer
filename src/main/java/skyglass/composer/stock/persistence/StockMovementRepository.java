package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

interface StockMovementRepository extends CrudRepository<StockMovementEntity, String> {

	StockMovementEntity findByUuid(String uuid);
}
