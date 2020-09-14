package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.model.Item;

public interface ItemService {

	Iterable<Item> getAll();

	Item getByUuid(String uuid);

}
