package skyglass.composer.stock.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

}
