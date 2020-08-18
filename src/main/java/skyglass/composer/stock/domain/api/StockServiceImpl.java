package skyglass.composer.stock.domain.api;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.Stock;
import skyglass.composer.stock.persistence.StockRepository;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockEntity;

@Service
@Transactional
class StockServiceImpl implements StockService {

	private final StockRepository stockRepository;

	@PersistenceContext
	private EntityManager entityManager;

	StockServiceImpl(StockRepository stockRepository) {
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
		return new Stock(entity.getUuid(), new Item(entity.getItem().getUuid(), entity.getItem().getName()), new BusinessUnit(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()),
				entity.getAmount());

	}

	StockEntity map(Stock entity) {
		return new StockEntity(entity.getUuid(), new ItemEntity(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnitEntity(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()), entity.getAmount());

	}

}
