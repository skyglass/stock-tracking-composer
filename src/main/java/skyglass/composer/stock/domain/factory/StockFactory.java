package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.common.factory.AObjectFactory;
import skyglass.composer.security.domain.factory.ContextFactory;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.entity.model.StockEntity;

@Component
public class StockFactory extends AObjectFactory<Stock, StockEntity> {

	@Autowired
	private ItemFactory itemFactory;

	@Autowired
	private ContextFactory contextFactory;

	@Override
	public Stock createObject(StockEntity entity) {
		return new Stock(entity.getUuid(), itemFactory.object(entity.getItem()),
				contextFactory.object(entity.getContext()),
				entity.getAmount(), entity.isActive());
	}

	@Override
	public StockEntity createEntity(Stock object) {
		return new StockEntity(object.getUuid(), itemFactory.entity(object.getItem()),
				contextFactory.entity(object.getContext()),
				object.getAmount(), object.isActive());
	}

}
