package skyglass.composer.stock.domain.repository;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.BusinessUnitHierarchyEntity;

public class BusinessUnitHierarchyRepository extends AEntityBean<BusinessUnitHierarchyEntity> {

	public BusinessUnitHierarchyEntity create(BusinessUnitEntity child, BusinessUnitEntity parent) {
		BusinessUnitHierarchyEntity result = createEntity(new BusinessUnitHierarchyEntity(null, child, parent));

		return result;
	}

}
