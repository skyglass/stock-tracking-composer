package skyglass.composer.stock.persistence;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.StockMovement;
import skyglass.composer.stock.StockMovementService;

@Component
class JpaStocMovementService implements StockMovementService {

	private final StockMovementRepository stockMovementRepository;

	@PersistenceContext
	private EntityManager entityManager;

	JpaStocMovementService(StockMovementRepository stockMovementRepository) {
		this.stockMovementRepository = stockMovementRepository;
	}

	@Override
	public Iterable<StockMovement> getAll() {
		return StreamSupport.stream(stockMovementRepository.findAll().spliterator(), false)
				.map(this::mapEntity)
				.collect(Collectors.toList());
	}

	@Override
	public StockMovement getByUuid(String uuid) {
		StockMovementEntity entity = this.stockMovementRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return mapEntity(entity);
	}

	StockMovement mapEntity(StockMovementEntity entity) {
		return new StockMovement(entity.getUuid(), entity.getItem(), entity.getFrom(), entity.getTo(), entity.getAmount(), entity.getCreatedAt(), entity.getParameters());

	}

	StockMovementEntity map(StockMovement entity) {
		return new StockMovementEntity(entity.getUuid(), entity.getItem(), entity.getFrom(), entity.getTo(), entity.getAmount(), entity.getCreatedAt(), entity.getParameters());

	}

}
