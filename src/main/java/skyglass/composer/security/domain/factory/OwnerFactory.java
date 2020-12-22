package skyglass.composer.security.domain.factory;

import org.springframework.stereotype.Component;

import skyglass.composer.common.factory.AObjectFactory;
import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.model.OwnerEntity;

@Component
public class OwnerFactory extends AObjectFactory<Owner, OwnerEntity> {

	@Override
	public Owner createObject(OwnerEntity entity) {
		return new Owner(entity.getUuid(), entity.getName());
	}

	@Override
	public OwnerEntity createEntity(Owner object) {
		return new OwnerEntity(object.getUuid(), object.getName());
	}

}
