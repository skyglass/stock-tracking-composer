package skyglass.composer.stock.domain.api;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockMovement;
import skyglass.composer.stock.domain.StockParameter;
import skyglass.composer.stock.persistence.StockMovementRepository;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockMovementEntity;
import skyglass.composer.stock.persistence.entity.StockParameterEntity;

@Service
@Transactional
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
		return new StockMovement(entity.getUuid(), new Item(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnit(entity.getFrom().getUuid(), entity.getFrom().getName()),
				new BusinessUnit(entity.getTo().getUuid(), entity.getTo().getName()),
				entity.getAmount(), entity.getOffset(), entity.getCreatedAt(),
				entity.getParameters().stream().map(p -> new StockParameter(p.getUuid(), p.getName(), p.getValue())).collect(Collectors.toList()));

	}

	StockMovementEntity map(StockMovement entity) {
		return new StockMovementEntity(entity.getUuid(),
				new ItemEntity(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnitEntity(entity.getFrom().getUuid(), entity.getFrom().getName()),
				new BusinessUnitEntity(entity.getTo().getUuid(), entity.getTo().getName()),
				entity.getAmount(), entity.getOffset(), entity.getCreatedAt(),
				entity.getParameters().stream().map(p -> new StockParameterEntity(p.getUuid(), p.getName(), p.getValue())).collect(Collectors.toList()));

	}

}
