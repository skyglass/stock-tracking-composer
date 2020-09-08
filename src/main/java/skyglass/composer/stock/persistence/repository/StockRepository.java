package skyglass.composer.stock.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import skyglass.composer.stock.persistence.entity.StockEntity;

public interface StockRepository extends CrudRepository<StockEntity, String> {

	StockEntity findByUuid(String uuid);
}