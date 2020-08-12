package skyglass.composer.stock.domain;

public interface ItemService {

	Iterable<Item> getAll();

	Item getByUuid(String uuid);

}
