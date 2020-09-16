package skyglass.composer.stock.domain.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.utils.date.DateUtil;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockTransaction extends AObject {
	
	private static final long serialVersionUID = -3280362793541117831L;

	private String uuid;

	private StockMessage message;

	private Date createdAt;
	
	private boolean pending;
	
	private boolean canceled;
	
	public static StockTransaction mapEntity(StockTransactionEntity entity) {
		return new StockTransaction(entity.getUuid(), StockMessage.mapEntity(entity.getMessage()), entity.getCreatedAt(), entity.isPending(), entity.isCanceled());

	}

	public static StockTransactionEntity map(StockTransaction entity) {
		return new StockTransactionEntity(entity.getUuid(), StockMessage.map(entity.getMessage()), entity.getCreatedAt(), entity.isPending(), entity.isCanceled());
	}
	
	public static List<StockTransaction> mapEntityList(List<StockTransactionEntity> list) {
		return list.stream().map(p -> mapEntity(p)).collect(Collectors.toList());
	}
	
	public static StockTransactionEntity create(StockMessageEntity stockMessage) {
		return new StockTransactionEntity(null, stockMessage, DateUtil.now(), true, false);
	}	

}
