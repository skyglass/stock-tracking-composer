package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.security.domain.factory.ContextFactory;
import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.model.StockHistory;
import skyglass.composer.stock.entity.model.StockHistoryEntity;

@Component
public class StockHistoryFactory extends AObjectFactory<StockHistory, StockHistoryEntity> {

	@Autowired
	private ItemFactory itemFactory;

	@Autowired
	private ContextFactory contextFactory;

	@Autowired
	private StockParameterFactory stockParameterFactory;

	@Override
	public StockHistory createObject(StockHistoryEntity entity) {
		return new StockHistory(entity.getUuid(),
				itemFactory.object(entity.getItem()),
				contextFactory.object(entity.getContext()),
				entity.getAmount(), entity.getStartDate(), entity.getEndDate(),
				stockParameterFactory.objectList(entity.getParameters()));
	}

	@Override
	public StockHistoryEntity createEntity(StockHistory object) {
		return new StockHistoryEntity(object.getUuid(),
				itemFactory.entity(object.getItem()),
				contextFactory.entity(object.getContext()),
				object.getAmount(), object.getStartDate(), object.getEndDate(),
				stockParameterFactory.entityList(object.getParameters()));
	}

}
