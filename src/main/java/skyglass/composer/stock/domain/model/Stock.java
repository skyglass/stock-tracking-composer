package skyglass.composer.stock.domain.model;

import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.security.domain.model.Context;
import skyglass.composer.stock.entity.model.StockEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Stock extends AObject {

	private static final long serialVersionUID = -1639576602087075222L;

	private static final String STOCK_CENTER_UUID = "158d60d5-5a81-4b1f-b7d6-36a349e05082";

	private String uuid;

	private Item item;

	private Context context;

	private Double amount;

	private boolean active;

	public static String key(String itemUuid, String contextUuid) {
		return itemUuid.concat("_").concat(contextUuid);
	}

	public static String key(Stock stock) {
		return key(stock.getItem().getUuid(), stock.getContext().getUuid());
	}

	public static boolean isStockCenter(StockEntity stock) {
		return Objects.equals(stock.getContext().getUuid(), STOCK_CENTER_UUID);
	}

}
