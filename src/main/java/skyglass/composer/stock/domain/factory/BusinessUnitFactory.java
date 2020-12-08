package skyglass.composer.stock.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.AEntityFactory;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;

@Component
public class BusinessUnitFactory extends AEntityFactory<BusinessUnit, BusinessUnitEntity> {

	@Autowired
	private BusinessOwnerFactory businessOwnerFactory;

	@Override
	public BusinessUnit createDto(BusinessUnitEntity entity) {
		return entity == null ? null
				: new BusinessUnit(entity.getUuid(), entity.getName(),
						businessOwnerFactory.dto(entity.getOwner()), dto(entity.getParent()));
	}

	@Override
	public BusinessUnitEntity createEntity(BusinessUnit dto) {
		return dto == null ? null
				: new BusinessUnitEntity(dto.getUuid(), dto.getName(),
						businessOwnerFactory.entity(dto.getOwner()), entity(dto.getParent()));
	}

}
