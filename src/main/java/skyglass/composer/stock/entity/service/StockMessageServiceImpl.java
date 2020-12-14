package skyglass.composer.stock.entity.service;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.factory.StockMessageFactory;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.repository.StockMessageRepository;

@Service
@Transactional
public class StockMessageServiceImpl implements StockMessageService {

	@Autowired
	private StockMessageRepository stockMessageRepository;

	@Autowired
	private StockMessageFactory stockMessageFactory;

	@Override
	public Collection<StockMessage> getAll() {
		return stockMessageRepository.findAll().stream()
				.map(sm -> stockMessageFactory.object(sm))
				.collect(Collectors.toList());
	}

	@Override
	public StockMessage getByUuid(String uuid) {
		StockMessageEntity entity = this.stockMessageRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return stockMessageFactory.object(entity);
	}

}
