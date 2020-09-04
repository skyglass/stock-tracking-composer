package skyglass.composer.stock.domain.service;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.persistence.entity.ItemEntity;

@Repository
@Transactional
public class ItemBean extends AEntityBean<ItemEntity> {

}
