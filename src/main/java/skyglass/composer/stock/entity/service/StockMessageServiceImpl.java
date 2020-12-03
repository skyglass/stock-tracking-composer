package skyglass.composer.stock.entity.service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.repository.StockMessageRepository;
import skyglass.composer.stock.entity.model.StockMessageEntity;

@Service
@Transactional
public class StockMessageServiceImpl implements StockMessageService {

	private final StockMessageRepository stockMessageBean;

	@PersistenceContext
	private EntityManager entityManager;

	StockMessageServiceImpl(StockMessageRepository stockMessageBean) {
		this.stockMessageBean = stockMessageBean;
	}

	@Override
	public Collection<StockMessage> getAll() {
		return StreamSupport.stream(stockMessageBean.findAll().spliterator(), false)
				.map(sm -> StockMessage.mapEntity(sm))
				.collect(Collectors.toList());
	}

	@Override
	public StockMessage getByUuid(String uuid) {
		StockMessageEntity entity = this.stockMessageBean.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return StockMessage.mapEntity(entity);
	}

}
