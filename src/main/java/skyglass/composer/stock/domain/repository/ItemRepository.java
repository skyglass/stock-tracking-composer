package skyglass.composer.stock.domain.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.entity.model.ItemEntity;

@Repository
@Transactional
public class ItemRepository extends AEntityRepository<ItemEntity> {

}
