package skyglass.composer.stock.persistence.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.repository.ItemRepository;

@Component
class ItemServiceImpl implements ItemService {

	private final ItemRepository itemRepository;

	@PersistenceContext
	private EntityManager entityManager;

	ItemServiceImpl(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Override
	public Iterable<Item> getAll() {
		return StreamSupport.stream(itemRepository.findAll().spliterator(), false)
				.map(e -> Item.mapEntity(e))
				.collect(Collectors.toList());
	}

	@Override
	public Item getByUuid(String uuid) {
		ItemEntity entity = this.itemRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return Item.mapEntity(entity);
	}

}
