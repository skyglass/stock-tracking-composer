package skyglass.composer.stock.domain.api;

import skyglass.composer.stock.domain.Item;

public interface ItemService {

	Iterable<Item> getAll();

	Item getByUuid(String uuid);

}
