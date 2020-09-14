package skyglass.composer.stock.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockHistoryEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockHistory extends AObject {

	private static final long serialVersionUID = 2538795431473160363L;

	private String uuid;

	private Item item;

	private BusinessUnit businessUnit;

	private Double amount;

	private Date startDate;

	private Date endDate;

	private List<StockParameter> parameters = new ArrayList<>();

	public static StockHistory mapEntity(StockHistoryEntity entity) {
		return new StockHistory(entity.getUuid(), new Item(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnit(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()),
				entity.getAmount(), entity.getStartDate(), entity.getEndDate(), StockParameter.list(entity.getParameters()));

	}

	public static StockHistoryEntity map(StockHistory entity) {
		return new StockHistoryEntity(entity.getUuid(),
				new ItemEntity(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnitEntity(entity.getBusinessUnit().getUuid(), entity.getBusinessUnit().getName()),
				entity.getAmount(), entity.getStartDate(), entity.getEndDate(), StockParameter.entityList(entity.getParameters()));

	}

	public static List<StockHistory> mapEntityList(List<StockHistoryEntity> list) {
		return list.stream().map(p -> mapEntity(p)).collect(Collectors.toList());
	}

}
