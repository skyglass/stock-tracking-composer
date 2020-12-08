package skyglass.composer.stock.domain.factory;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.AEntityFactory;
import skyglass.composer.stock.domain.model.BusinessOwner;
import skyglass.composer.stock.entity.model.BusinessOwnerEntity;

@Component
public class BusinessOwnerFactory extends AEntityFactory<BusinessOwner, BusinessOwnerEntity> {

	@Override
	public BusinessOwner createDto(BusinessOwnerEntity entity) {
		return new BusinessOwner(entity.getUuid(), entity.getName());
	}

	@Override
	public BusinessOwnerEntity createEntity(BusinessOwner dto) {
		return new BusinessOwnerEntity(dto.getUuid(), dto.getName());
	}

}
