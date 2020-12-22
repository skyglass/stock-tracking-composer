package skyglass.composer.stock.domain.factory;

import org.springframework.stereotype.Component;

import skyglass.composer.common.factory.AObjectFactory;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.entity.model.ItemEntity;

@Component
public class ItemFactory extends AObjectFactory<Item, ItemEntity> {

	@Override
	public Item createObject(ItemEntity entity) {
		return new Item(entity.getUuid(), entity.getName());
	}

	@Override
	public ItemEntity createEntity(Item object) {
		return new ItemEntity(object.getUuid(), object.getName());
	}

}
