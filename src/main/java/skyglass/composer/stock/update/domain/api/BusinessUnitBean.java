package skyglass.composer.stock.update.domain.api;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;

@Repository
@Transactional
public class BusinessUnitBean extends AEntityBean<BusinessUnitEntity> {

}
