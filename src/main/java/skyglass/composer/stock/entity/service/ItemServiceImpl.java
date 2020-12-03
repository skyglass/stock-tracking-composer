package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.repository.ItemRepository;
import skyglass.composer.stock.entity.model.ItemEntity;

@Component
class ItemServiceImpl implements ItemService {

	private final ItemRepository itemBean;

	@PersistenceContext
	private EntityManager entityManager;

	ItemServiceImpl(ItemRepository itemBean) {
		this.itemBean = itemBean;
	}

	@Override
	public Iterable<Item> getAll() {
		return StreamSupport.stream(itemBean.findAll().spliterator(), false)
				.map(e -> Item.mapEntity(e))
				.collect(Collectors.toList());
	}

	@Override
	public Item getByUuid(String uuid) {
		ItemEntity entity = this.itemBean.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return Item.mapEntity(entity);
	}

}
