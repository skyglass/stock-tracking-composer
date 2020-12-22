package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.common.factory.AObjectFactory;
import skyglass.composer.stock.domain.model.TransactionItem;
import skyglass.composer.stock.domain.model.TransactionType;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;
import skyglass.composer.utils.date.DateUtil;

@Component
public class TransactionItemFactory extends AObjectFactory<TransactionItem, TransactionItemEntity> {

	@Autowired
	private StockTransactionFactory stockTransactionFactory;

	@Override
	public TransactionItem createObject(TransactionItemEntity entity) {
		return new TransactionItem(entity.getUuid(),
				stockTransactionFactory.object(entity.getTransaction()),
				entity.getKey(), entity.getTransactionType(), entity.getCreatedAt(),
				entity.isPending());
	}

	@Override
	public TransactionItemEntity createEntity(TransactionItem object) {
		return new TransactionItemEntity(object.getUuid(),
				stockTransactionFactory.entity(object.getTransaction()),
				object.getKey(), object.getTransactionType(), object.getCreatedAt(), object.isPending());
	}

	public TransactionItemEntity entity(StockTransactionEntity transaction, String key, TransactionType transactionType) {
		return new TransactionItemEntity(null, transaction, key, transactionType, DateUtil.now(), true);
	}

}
