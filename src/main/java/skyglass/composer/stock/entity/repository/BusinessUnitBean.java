package skyglass.composer.stock.entity.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;

@Repository
@Transactional
public class BusinessUnitBean extends AEntityBean<BusinessUnitEntity> {

}
