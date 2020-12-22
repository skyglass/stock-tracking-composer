package skyglass.composer.stock.entity.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.stock.entity.model.ItemEntity;

@Repository
@Transactional
public class ItemRepository extends AEntityRepository<ItemEntity> {

}
