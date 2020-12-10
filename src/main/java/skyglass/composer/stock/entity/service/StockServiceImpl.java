package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.factory.StockFactory;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.repository.StockRepository;
import skyglass.composer.stock.entity.model.StockEntity;

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
	private BusinessUnitService businessUnitService;

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
	public Stock findByItemAndBusinessUnit(String itemUuid, String businessUnitUuid) {
		Item item = itemService.getByUuid(itemUuid);
		BusinessUnit businessUnit = businessUnitService.getByUuid(businessUnitUuid);
		return stockFactory.object(stockRepository.findByItemAndBusinessUnit(item.getUuid(), businessUnit.getUuid()));
	}

	@Override
	public Stock deactivate(String itemUuid, String businessUnitUuid) {
		Item item = itemService.getByUuid(itemUuid);
		BusinessUnit businessUnit = businessUnitService.getByUuid(businessUnitUuid);
		return stockFactory.object(stockRepository.deactivateStock(item.getUuid(), businessUnit.getUuid()));
	}

}
