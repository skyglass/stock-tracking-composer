package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;

@Component
public class BusinessUnitFactory extends AObjectFactory<BusinessUnit, BusinessUnitEntity> {

	@Autowired
	private BusinessOwnerFactory businessOwnerFactory;

	@Override
	public BusinessUnit createObject(BusinessUnitEntity entity) {
		return entity == null ? null
				: new BusinessUnit(entity.getUuid(), entity.getName(),
						businessOwnerFactory.object(entity.getOwner()), object(entity.getParent()));
	}

	@Override
	public BusinessUnitEntity createEntity(BusinessUnit object) {
		return object == null ? null
				: new BusinessUnitEntity(object.getUuid(), object.getName(),
						businessOwnerFactory.entity(object.getOwner()), entity(object.getParent()));
	}

}
