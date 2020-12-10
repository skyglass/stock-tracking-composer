package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.model.StockTransaction;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.utils.date.DateUtil;

public class StockTransactionFactory extends AObjectFactory<StockTransaction, StockTransactionEntity> {

	@Autowired
	private StockMessageFactory stockMessageFactory;

	@Override
	public StockTransaction createObject(StockTransactionEntity entity) {
		return new StockTransaction(entity.getUuid(),
				stockMessageFactory.object(entity.getMessage()),
				entity.getCreatedAt(), entity.isPending());
	}

	@Override
	public StockTransactionEntity createEntity(StockTransaction object) {
		return new StockTransactionEntity(object.getUuid(),
				stockMessageFactory.entity(object.getMessage()),
				object.getCreatedAt(), object.isPending());
	}

	public StockTransactionEntity createEntity(StockMessageEntity stockMessage) {
		return new StockTransactionEntity(null, stockMessage, DateUtil.now(), true);
	}

}
