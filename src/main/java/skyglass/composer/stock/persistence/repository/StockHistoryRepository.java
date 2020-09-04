package skyglass.composer.stock.persistence.repository;

import org.springframework.data.repository.CrudRepository;

import skyglass.composer.stock.persistence.entity.StockHistoryEntity;

public interface StockHistoryRepository extends CrudRepository<StockHistoryEntity, String> {

	StockHistoryEntity findByUuid(String uuid);
}
