package skyglass.composer.stock.domain.service;

import java.util.Objects;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockMessage;

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
	
	public boolean shouldUpdateStock() {
		//if business units are the same , then don't need to update the stocks
		return isBetweenUnits();
	}

}
