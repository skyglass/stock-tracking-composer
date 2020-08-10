package skyglass.composer.stock.persistence;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.StockHistory;
import skyglass.composer.stock.StockHistoryService;

@Component
class JpaStockHistoryService implements StockHistoryService {

	private final StockHistoryRepository stockHistoryRepository;

	@PersistenceContext
	private EntityManager entityManager;

	JpaStockHistoryService(StockHistoryRepository stockHistoryRepository) {
		this.stockHistoryRepository = stockHistoryRepository;
	}

	@Override
	public Iterable<StockHistory> getAll() {
		return StreamSupport.stream(stockHistoryRepository.findAll().spliterator(), false)
				.map(this::mapEntity)
				.collect(Collectors.toList());
	}

	@Override
	public StockHistory getByUuid(String uuid) {
		StockHistoryEntity entity = this.stockHistoryRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return mapEntity(entity);
	}

	StockHistory mapEntity(StockHistoryEntity entity) {
		return new StockHistory(entity.getUuid(), entity.getItem(), entity.getBusinessUnit(), entity.getAmount(), entity.getStartDate(), entity.getEndDate());

	}

	StockHistoryEntity map(StockHistory entity) {
		return new StockHistoryEntity(entity.getUuid(), entity.getItem(), entity.getBusinessUnit(), entity.getAmount(), entity.getStartDate(), entity.getEndDate());

	}

}
