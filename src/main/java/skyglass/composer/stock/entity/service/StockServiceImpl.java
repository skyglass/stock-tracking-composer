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
import skyglass.composer.stock.domain.repository.StockBean;
import skyglass.composer.stock.entity.model.StockEntity;

@Service
@Transactional
class StockServiceImpl implements StockService {

	@Autowired
	private StockBean stockBean;

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
	public Stock findByItemAndBusinessUnit(Item item, BusinessUnit businessUnit) {
		return Stock.mapEntity(stockBean.findByItemUuidAndBusinessUnitUuid(item.getUuid(), businessUnit.getUuid()));
	}

}
