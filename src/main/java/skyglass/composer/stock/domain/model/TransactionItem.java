package skyglass.composer.stock.domain.model;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.StockTransactionEntity;
import skyglass.composer.stock.entity.model.TransactionItemEntity;
import skyglass.composer.utils.date.DateUtil;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TransactionItem extends AObject {
	
	private static final long serialVersionUID = -6983525806755992541L;

	private String uuid;
	
	private StockTransaction transaction;
	
	private String key;
	
	private TransactionType transactionType;

	private Date createdAt;
	
	private boolean pending;
	
	public static List<TransactionItem> list(List<TransactionItemEntity> list) {
		return list.stream().map(p -> new TransactionItem(p.getUuid(), StockTransaction.mapEntity(p.getTransaction()), p.getKey(), p.getTransactionType(), p.getCreatedAt(), p.isPending())).collect(Collectors.toList());
	}

	public static List<TransactionItemEntity> entityList(List<TransactionItem> list) {
		return list.stream().map(p -> new TransactionItemEntity(p.getUuid(), StockTransaction.map(p.getTransaction()), p.getKey(), p.getTransactionType(), p.getCreatedAt(), p.isPending())).collect(Collectors.toList());
	}
	
	public static TransactionItemEntity create(StockTransactionEntity transaction, String key, TransactionType transactionType) {
		return new TransactionItemEntity(null, transaction, key, transactionType, DateUtil.now(), true);
	}
	
	
	
}
