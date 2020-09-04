package skyglass.composer.stock.persistence.service;

import skyglass.composer.stock.domain.Item;

public interface ItemService {

	Iterable<Item> getAll();

	Item getByUuid(String uuid);

}
