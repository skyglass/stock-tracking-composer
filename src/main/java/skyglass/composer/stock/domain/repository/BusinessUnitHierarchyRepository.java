package skyglass.composer.stock.domain.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.BusinessUnitHierarchyEntity;

@Repository
@Transactional
public class BusinessUnitHierarchyRepository extends AEntityBean<BusinessUnitHierarchyEntity> {

	public BusinessUnitHierarchyEntity create(BusinessUnitEntity child, BusinessUnitEntity parent) {
		BusinessUnitHierarchyEntity result = createEntity(new BusinessUnitHierarchyEntity(null, child, parent));

		return result;
	}

}
