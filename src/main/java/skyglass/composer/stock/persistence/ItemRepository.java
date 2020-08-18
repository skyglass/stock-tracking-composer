package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

import skyglass.composer.stock.persistence.entity.ItemEntity;

public interface ItemRepository extends CrudRepository<ItemEntity, String> {

	ItemEntity findByUuid(String uuid);
}
