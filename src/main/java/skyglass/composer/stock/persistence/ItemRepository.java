package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

interface ItemRepository extends CrudRepository<ItemEntity, String> {

	ItemEntity findByUuid(String uuid);
}
