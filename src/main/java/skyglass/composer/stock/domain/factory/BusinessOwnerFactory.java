package skyglass.composer.stock.domain.factory;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.AObjectFactory;
import skyglass.composer.stock.domain.model.BusinessOwner;
import skyglass.composer.stock.entity.model.BusinessOwnerEntity;

@Component
public class BusinessOwnerFactory extends AObjectFactory<BusinessOwner, BusinessOwnerEntity> {

	@Override
	public BusinessOwner createObject(BusinessOwnerEntity entity) {
		return new BusinessOwner(entity.getUuid(), entity.getName());
	}

	@Override
	public BusinessOwnerEntity createEntity(BusinessOwner object) {
		return new BusinessOwnerEntity(object.getUuid(), object.getName());
	}

}
