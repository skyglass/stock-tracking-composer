package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.repository.StockMessageBean;

@Service
@Transactional
public class StockMessageServiceImpl implements StockMessageService {

	private final StockMessageBean stockMessageBean;

	@PersistenceContext
	private EntityManager entityManager;

	StockMessageServiceImpl(StockMessageBean stockMessageBean) {
		this.stockMessageBean = stockMessageBean;
	}

	@Override
	public Iterable<StockMessage> getAll() {
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
