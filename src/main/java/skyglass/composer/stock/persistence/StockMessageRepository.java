package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

import skyglass.composer.stock.persistence.entity.StockMessageEntity;

public interface StockMessageRepository extends CrudRepository<StockMessageEntity, String> {

	StockMessageEntity findByUuid(String uuid);
}
