package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.entity.model.StockEntity;

@Component
public class StockFactory extends AObjectFactory<Stock, StockEntity> {

	@Autowired
	private ItemFactory itemFactory;

	@Autowired
	private BusinessUnitFactory businessUnitFactory;

	@Override
	public Stock createObject(StockEntity entity) {
		return new Stock(entity.getUuid(), itemFactory.object(entity.getItem()),
				businessUnitFactory.object(entity.getBusinessUnit()),
				entity.getAmount(), entity.isActive());
	}

	@Override
	public StockEntity createEntity(Stock object) {
		return new StockEntity(object.getUuid(), itemFactory.entity(object.getItem()),
				businessUnitFactory.entity(object.getBusinessUnit()),
				object.getAmount(), object.isActive());
	}

}
