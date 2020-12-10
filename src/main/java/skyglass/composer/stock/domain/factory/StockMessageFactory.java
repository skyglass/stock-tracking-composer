package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.dto.StockMessageDto;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.repository.BusinessUnitRepository;
import skyglass.composer.stock.domain.repository.ItemRepository;
import skyglass.composer.stock.domain.repository.StockTransactionRepository;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.utils.date.DateUtil;

public class StockMessageFactory extends AObjectFactory<StockMessage, StockMessageEntity> {

	@Autowired
	private ItemFactory itemFactory;

	@Autowired
	private BusinessUnitFactory businessUnitFactory;

	@Autowired
	private StockParameterFactory stockParameterFactory;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private StockTransactionRepository stockTransactionRepository;

	@Override
	public StockMessage createObject(StockMessageEntity entity) {
		return new StockMessage(entity.getUuid(),
				itemFactory.object(entity.getItem()),
				businessUnitFactory.object(entity.getFrom()),
				businessUnitFactory.object(entity.getTo()),
				entity.getAmount(), entity.getOffsetKey(), entity.getCreatedAt(), entity.getMessageId(),
				stockParameterFactory.objectList(entity.getParameters()));
	}

	@Override
	public StockMessageEntity createEntity(StockMessage object) {
		return new StockMessageEntity(object.getUuid(),
				itemFactory.entity(object.getItem()),
				businessUnitFactory.entity(object.getFrom()),
				businessUnitFactory.entity(object.getTo()),
				object.getAmount(), object.getOffset(), object.getCreatedAt(), object.getMessageId(),
				stockParameterFactory.entityList(object.getParameters()));
	}

	public StockMessageEntity entity(StockMessageDto dto, ItemEntity item, BusinessUnitEntity from, BusinessUnitEntity to) {
		return new StockMessageEntity(null, item, from, to, dto.getAmount(), 0L,
				dto.getCreatedAt() == null ? DateUtil.now() : dto.getCreatedAt(), dto.getId(),
				stockParameterFactory.entityList(dto.getStockParameters()));
	}

	public StockMessage object(StockMessageDto dto) {
		ItemEntity item = itemRepository.findByUuidSecure(dto.getItemUuid());
		BusinessUnitEntity from = businessUnitRepository.findByUuidSecure(dto.getFromUuid());
		BusinessUnitEntity to = businessUnitRepository.findByUuidSecure(dto.getToUuid());
		StockMessageEntity entity = entity(dto, item, from, to);
		stockTransactionRepository.create(entity);
		return object(entity);
	}
}
