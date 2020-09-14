package skyglass.composer.stock.domain.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.StockTransactionEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockTransaction extends AObject {
	
	private static final long serialVersionUID = -3280362793541117831L;

	private String uuid;

	private StockMessage message;

	private Date createdAt;
	
	private boolean pending;
	
	public static StockTransaction mapEntity(StockTransactionEntity entity) {
		return new StockTransaction(entity.getUuid(), StockMessage.mapEntity(entity.getMessage()), entity.getCreatedAt(), entity.isPending());

	}

	public static StockTransactionEntity map(StockTransaction entity) {
		return new StockTransactionEntity(entity.getUuid(), StockMessage.map(entity.getMessage()), entity.getCreatedAt(), entity.isPending());
	}
	
	public static List<StockTransaction> mapEntityList(List<StockTransactionEntity> list) {
		return list.stream().map(p -> mapEntity(p)).collect(Collectors.toList());
	}

}
