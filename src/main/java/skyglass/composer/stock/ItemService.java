package skyglass.composer.stock;

public interface ItemService {

	Iterable<Item> getAll();

	Item getByUuid(String uuid);

}
