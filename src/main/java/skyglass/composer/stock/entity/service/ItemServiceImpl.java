package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.factory.ItemFactory;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.repository.ItemRepository;

@Component
class ItemServiceImpl implements ItemService {

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private ItemFactory itemFactory;

	@Override
	public Iterable<Item> getAll() {
		return itemRepository.findAll().stream().map(e -> itemFactory.object(e))
				.collect(Collectors.toList());
	}

	@Override
	public Item getByUuid(String uuid) {
		ItemEntity entity = this.itemRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return itemFactory.object(entity);
	}

}
