package skyglass.composer.stock.update.domain.api;

import java.util.Objects;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockMessage;
import skyglass.composer.stock.persistence.entity.StockEntity;

public class StockUpdate {

	private String fromUuid;

	private String toUuid;

	private String itemUuid;

	private String stockMessageUuid;

	protected StockUpdate(Item item, BusinessUnit from, BusinessUnit to, StockMessage stockMessage) {
		this.toUuid = to.getUuid();
		this.fromUuid = from.getUuid();
		this.stockMessageUuid = stockMessage.getUuid();
		this.itemUuid = item.getUuid();
	}

	public String getStockMessageUuid() {
		return stockMessageUuid;
	}

	public String getItemUuid() {
		return itemUuid;
	}

	public String getToUuid() {
		return toUuid;
	}

	public String getFromUuid() {
		return fromUuid;
	}

	public boolean isBetweenUnits() {
		return !Objects.equals(fromUuid, toUuid);
	}

	public StockEntity setStockAmount(StockEntity stock, double updatedAmount) {
		return new StockEntity(stock.getUuid(), stock.getItem(), stock.getBusinessUnit(), updatedAmount < 0 ? 0 : updatedAmount);
	}

	public boolean shouldUpdateStock() {
		//if business units are the same , then don't need to update the stocks
		return isBetweenUnits();
	}

}
