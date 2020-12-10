package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.model.StockHistory;
import skyglass.composer.stock.entity.model.StockHistoryEntity;

public class StockHistoryFactory extends AObjectFactory<StockHistory, StockHistoryEntity> {

	@Autowired
	private ItemFactory itemFactory;

	@Autowired
	private BusinessUnitFactory businessUnitFactory;

	@Autowired
	private StockParameterFactory stockParameterFactory;

	@Override
	public StockHistory createObject(StockHistoryEntity entity) {
		return new StockHistory(entity.getUuid(),
				itemFactory.object(entity.getItem()),
				businessUnitFactory.object(entity.getBusinessUnit()),
				entity.getAmount(), entity.getStartDate(), entity.getEndDate(),
				stockParameterFactory.objectList(entity.getParameters()));
	}

	@Override
	public StockHistoryEntity createEntity(StockHistory object) {
		return new StockHistoryEntity(object.getUuid(),
				itemFactory.entity(object.getItem()),
				businessUnitFactory.entity(object.getBusinessUnit()),
				object.getAmount(), object.getStartDate(), object.getEndDate(),
				stockParameterFactory.entityList(object.getParameters()));
	}

}
