package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

interface StockHistoryRepository extends CrudRepository<StockHistoryEntity, String> {

	StockHistoryEntity findByUuid(String uuid);
}
