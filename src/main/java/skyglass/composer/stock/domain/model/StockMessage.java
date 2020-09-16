package skyglass.composer.stock.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.domain.dto.StockMessageDto;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockParameterEntity;
import skyglass.composer.utils.date.DateUtil;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockMessage extends AObject {

	private static final long serialVersionUID = 7924635977183057862L;

	private String uuid;

	private Item item;

	private BusinessUnit from;

	private BusinessUnit to;

	private Double amount;

	private Long offset;

	private Date createdAt;

	private String messageId;

	private List<StockParameter> parameters = new ArrayList<>();

	public static StockMessage mapEntity(StockMessageEntity entity) {
		return new StockMessage(entity.getUuid(), new Item(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnit(entity.getFrom().getUuid(), entity.getFrom().getName()),
				new BusinessUnit(entity.getTo().getUuid(), entity.getTo().getName()),
				entity.getAmount(), entity.getOffsetKey(), entity.getCreatedAt(), entity.getMessageId(),
				entity.getParameters().stream().map(p -> new StockParameter(p.getUuid(), p.getName(), p.getValue())).collect(Collectors.toList()));

	}

	public static StockMessageEntity map(StockMessage entity) {
		return new StockMessageEntity(entity.getUuid(),
				new ItemEntity(entity.getItem().getUuid(), entity.getItem().getName()),
				new BusinessUnitEntity(entity.getFrom().getUuid(), entity.getFrom().getName()),
				new BusinessUnitEntity(entity.getTo().getUuid(), entity.getTo().getName()),
				entity.getAmount(), entity.getOffset(), entity.getCreatedAt(), entity.getMessageId(),
				entity.getParameters().stream().map(p -> new StockParameterEntity(p.getUuid(), p.getName(), p.getValue())).collect(Collectors.toList()));

	}
	
	public static StockMessageEntity createEntity(StockMessageDto dto, ItemEntity item, BusinessUnitEntity from, BusinessUnitEntity to) {
		return new StockMessageEntity(null, item, from, to, dto.getAmount(), 0L,
				dto.getCreatedAt() == null ? DateUtil.now() : dto.getCreatedAt(), dto.getId(),
				dto.getStockParameters().stream().map(sp -> new StockParameterEntity(sp.getUuid(), sp.getName(), sp.getValue())).collect(Collectors.toList()));		
	}

}
