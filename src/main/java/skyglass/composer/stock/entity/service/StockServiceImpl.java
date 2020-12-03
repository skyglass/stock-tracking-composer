package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.repository.StockRepository;
import skyglass.composer.stock.entity.model.StockEntity;

@Service
@Transactional
class StockServiceImpl implements StockService {

	@Autowired
	private StockRepository stockBean;

	@Autowired
	private ItemService itemService;

	@Autowired
	private BusinessUnitService businessUnitService;

	@PersistenceContext
	private EntityManager entityManager;

	@Override
	public Iterable<Stock> getAll() {
		return StreamSupport.stream(stockBean.findAll().spliterator(), false)
				.map(e -> Stock.mapEntity(e))
				.collect(Collectors.toList());
	}

	@Override
	public Stock getByUuid(String uuid) {
		StockEntity entity = this.stockBean.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return Stock.mapEntity(entity);
	}

	@Override
	public Stock findByItemAndBusinessUnit(String itemUuid, String businessUnitUuid) {
		Item item = itemService.getByUuid(itemUuid);
		BusinessUnit businessUnit = businessUnitService.getByUuid(businessUnitUuid);
		return Stock.mapEntity(stockBean.findByItemAndBusinessUnit(item.getUuid(), businessUnit.getUuid()));
	}

	@Override
	public Stock deactivate(String itemUuid, String businessUnitUuid) {
		Item item = itemService.getByUuid(itemUuid);
		BusinessUnit businessUnit = businessUnitService.getByUuid(businessUnitUuid);
		return Stock.mapEntity(stockBean.deactivateStock(item.getUuid(), businessUnit.getUuid()));
	}

}
