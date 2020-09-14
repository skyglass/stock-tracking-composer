package skyglass.composer.stock.entity.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.entity.model.ItemEntity;

@Repository
@Transactional
public class ItemBean extends AEntityBean<ItemEntity> {

}
