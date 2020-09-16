package skyglass.composer.stock.domain.service;

import java.util.Objects;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;

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
	
	public static StockUpdate create(ItemEntity item, BusinessUnitEntity from, BusinessUnitEntity to, StockMessageEntity stockMessage) {
		return new StockUpdate(Item.mapEntity(item), BusinessUnit.mapEntity(from), BusinessUnit.mapEntity(to), StockMessage.mapEntity(stockMessage));
	}

}
