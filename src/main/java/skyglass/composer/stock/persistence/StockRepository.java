package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

interface StockRepository extends CrudRepository<StockEntity, String> {

	StockEntity findByUuid(String uuid);
}
