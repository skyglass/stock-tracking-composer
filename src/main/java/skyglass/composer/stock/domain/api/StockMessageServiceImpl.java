package skyglass.composer.stock.domain.api;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.StockMessage;
import skyglass.composer.stock.persistence.StockMessageRepository;
import skyglass.composer.stock.persistence.entity.StockMessageEntity;

@Service
@Transactional
public class StockMessageServiceImpl implements StockMessageService {

	private final StockMessageRepository stockMessageRepository;

	@PersistenceContext
	private EntityManager entityManager;

	StockMessageServiceImpl(StockMessageRepository stockMessageRepository) {
		this.stockMessageRepository = stockMessageRepository;
	}

	@Override
	public Iterable<StockMessage> getAll() {
		return StreamSupport.stream(stockMessageRepository.findAll().spliterator(), false)
				.map(sm -> StockMessage.mapEntity(sm))
				.collect(Collectors.toList());
	}

	@Override
	public StockMessage getByUuid(String uuid) {
		StockMessageEntity entity = this.stockMessageRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return StockMessage.mapEntity(entity);
	}

}
