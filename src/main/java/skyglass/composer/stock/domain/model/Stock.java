package skyglass.composer.stock.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Stock extends AObject {

	private static final long serialVersionUID = -1639576602087075222L;

	private String uuid;

	private Item item;

	private BusinessUnit businessUnit;

	private Double amount;
	
	private boolean active;
	
	public static Stock mapEntity(StockEntity entity) {
		return new Stock(entity.getUuid(), new Item(entity.getItem().getUuid(), entity.getItem().getName()), new BusinessUnit(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()),
				entity.getAmount(), entity.isActive());

	}

	public static StockEntity map(Stock entity) {
		return new StockEntity(entity.getUuid(), new ItemEntity(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnitEntity(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()), entity.getAmount(), entity.isActive());

	}
	
	public static String key(String itemUuid, String businessUnitUuid) {
		return itemUuid.concat("_").concat(businessUnitUuid);
	}
	
	public static String key(Stock stock) {
		return key(stock.getItem().getUuid(), stock.getBusinessUnit().getUuid());
	}

}
