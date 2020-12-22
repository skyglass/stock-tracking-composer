package skyglass.composer.stock.domain.factory;

import org.springframework.stereotype.Component;

import skyglass.composer.common.factory.ACopyObjectFactory;
import skyglass.composer.stock.domain.model.StockParameter;
import skyglass.composer.stock.entity.model.StockParameterEntity;

@Component
public class StockParameterFactory extends ACopyObjectFactory<StockParameter, StockParameterEntity> {

	@Override
	public StockParameterEntity copyEntity(StockParameterEntity entity) {
		return new StockParameterEntity(null, entity.getName(), entity.getValue());
	}

	@Override
	public StockParameter createObject(StockParameterEntity entity) {
		return new StockParameter(entity.getUuid(), entity.getName(), entity.getValue());
	}

	@Override
	public StockParameterEntity createEntity(StockParameter object) {
		return new StockParameterEntity(object.getUuid(), object.getName(), object.getValue());
	}

}
