package skyglass.composer.stock.domain.api;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.StockHistory;
import skyglass.composer.stock.persistence.StockHistoryRepository;
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
				.map(entity -> StockHistory.mapEntity(entity))
				.collect(Collectors.toList());
	}

	@Override
	public StockHistory getByUuid(String uuid) {
		StockHistoryEntity entity = this.stockHistoryRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return StockHistory.mapEntity(entity);
	}

}
