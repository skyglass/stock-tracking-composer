package skyglass.composer.stock.persistence;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.Item;
import skyglass.composer.stock.ItemService;

@Component
class JpaItemService implements ItemService {

	private final ItemRepository itemRepository;

	@PersistenceContext
	private EntityManager entityManager;

	JpaItemService(ItemRepository itemRepository) {
		this.itemRepository = itemRepository;
	}

	@Override
	public Iterable<Item> getAll() {
		return StreamSupport.stream(itemRepository.findAll().spliterator(), false)
				.map(this::mapEntity)
				.collect(Collectors.toList());
	}

	@Override
	public Item getByUuid(String uuid) {
		ItemEntity entity = this.itemRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return mapEntity(entity);
	}

	Item mapEntity(ItemEntity entity) {
		return new Item(entity.getUuid(), entity.getName());

	}

	ItemEntity map(Item entity) {
		return new ItemEntity(entity.getUuid(), entity.getName());

	}

}
