package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.security.domain.factory.ContextFactory;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.security.entity.repository.ContextRepository;
import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.dto.StockMessageDto;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.repository.ItemRepository;
import skyglass.composer.utils.date.DateUtil;

@Component
public class StockMessageFactory extends AObjectFactory<StockMessage, StockMessageEntity> {

	@Autowired
	private ItemFactory itemFactory;

	@Autowired
	private ContextFactory contextFactory;

	@Autowired
	private StockParameterFactory stockParameterFactory;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ContextRepository contextRepository;

	@Override
	public StockMessage createObject(StockMessageEntity entity) {
		return new StockMessage(entity.getUuid(),
				itemFactory.object(entity.getItem()),
				contextFactory.object(entity.getFrom()),
				contextFactory.object(entity.getTo()),
				entity.getAmount(), entity.getOffsetKey(), entity.getCreatedAt(), entity.getMessageId(),
				stockParameterFactory.objectList(entity.getParameters()));
	}

	@Override
	public StockMessageEntity createEntity(StockMessage object) {
		return new StockMessageEntity(object.getUuid(),
				itemFactory.entity(object.getItem()),
				contextFactory.entity(object.getFrom()),
				contextFactory.entity(object.getTo()),
				object.getAmount(), object.getOffset(), object.getCreatedAt(), object.getMessageId(),
				stockParameterFactory.entityList(object.getParameters()));
	}

	public StockMessageEntity entity(StockMessageDto dto, ItemEntity item, ContextEntity from, ContextEntity to) {
		return new StockMessageEntity(null, item, from, to, dto.getAmount(), 0L,
				dto.getCreatedAt() == null ? DateUtil.now() : dto.getCreatedAt(), dto.getId(),
				stockParameterFactory.entityList(dto.getStockParameters()));
	}

	public StockMessageEntity entity(StockMessageDto dto) {
		ItemEntity item = itemRepository.findByUuidSecure(dto.getItemUuid());
		ContextEntity from = contextRepository.findByUuidSecure(dto.getFromUuid());
		ContextEntity to = contextRepository.findByUuidSecure(dto.getToUuid());
		StockMessageEntity entity = entity(dto, item, from, to);
		return entity;
	}
}
