package skyglass.composer.stock.persistence;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.Stock;
import skyglass.composer.stock.domain.StockService;

@Component
class JpaStockService implements StockService {

	private final StockRepository stockRepository;

	@PersistenceContext
	private EntityManager entityManager;

	JpaStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}

	@Override
	public Iterable<Stock> getAll() {
		return StreamSupport.stream(stockRepository.findAll().spliterator(), false)
				.map(this::mapEntity)
				.collect(Collectors.toList());
	}

	@Override
	public Stock getByUuid(String uuid) {
		StockEntity entity = this.stockRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return mapEntity(entity);
	}

	Stock mapEntity(StockEntity entity) {
		return new Stock(entity.getUuid(), entity.getItem(), entity.getBusinessUnit(), entity.getAmount());

	}

	StockEntity map(Stock entity) {
		return new StockEntity(entity.getUuid(), entity.getItem(), entity.getBusinessUnit(), entity.getAmount());

	}

}
