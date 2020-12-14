package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.service.ContextService;
import skyglass.composer.stock.domain.factory.StockFactory;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.entity.model.StockEntity;
import skyglass.composer.stock.entity.repository.StockRepository;

@Service
@Transactional
class StockServiceImpl implements StockService {

	@Autowired
	private StockRepository stockRepository;

	@Autowired
	private StockFactory stockFactory;

	@Autowired
	private ItemService itemService;

	@Autowired
	private ContextService contextService;

	@Override
	public Iterable<Stock> getAll() {
		return stockRepository.findAll().stream().map(e -> stockFactory.object(e))
				.collect(Collectors.toList());
	}

	@Override
	public Stock getByUuid(String uuid) {
		StockEntity entity = this.stockRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return stockFactory.object(entity);
	}

	@Override
	public Stock findByItemAndContext(String itemUuid, String contextUuid) {
		Item item = itemService.getByUuid(itemUuid);
		Context context = contextService.getByUuid(contextUuid);
		return stockFactory.object(stockRepository.findByItemAndContext(item.getUuid(), context.getUuid()));
	}

	@Override
	public Stock deactivate(String itemUuid, String contextUuid) {
		Item item = itemService.getByUuid(itemUuid);
		Context context = contextService.getByUuid(contextUuid);
		return stockFactory.object(stockRepository.deactivateStock(item.getUuid(), context.getUuid()));
	}

}
