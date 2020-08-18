package skyglass.composer.stock.domain.api;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockHistory;
import skyglass.composer.stock.persistence.StockHistoryRepository;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockHistoryEntity;

@Service
@Transactional
class StockHistoryServiceImpl implements StockHistoryService {

	private final StockHistoryRepository stockHistoryRepository;

	@PersistenceContext
	private EntityManager entityManager;

	StockHistoryServiceImpl(StockHistoryRepository stockHistoryRepository) {
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
		return new StockHistory(entity.getUuid(), new Item(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnit(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()), entity.getAmount(), entity.getStartDate(), entity.getEndDate());

	}

	StockHistoryEntity map(StockHistory entity) {
		return new StockHistoryEntity(entity.getUuid(), new ItemEntity(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnitEntity(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()), entity.getAmount(), entity.getStartDate(), entity.getEndDate());

	}

}
