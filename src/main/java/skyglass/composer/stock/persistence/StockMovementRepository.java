package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

import skyglass.composer.stock.persistence.entity.StockMovementEntity;

public interface StockMovementRepository extends CrudRepository<StockMovementEntity, String> {

	StockMovementEntity findByUuid(String uuid);
}
