package skyglass.composer.stock.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BusinessUnit extends AObject {

	private static final long serialVersionUID = -4855746732917542351L;

	private String uuid;

	private String name;

	private BusinessOwner owner;

	private BusinessUnit parent;

	public static BusinessUnit mapEntity(BusinessUnitEntity entity) {
		return entity == null ? null : new BusinessUnit(entity.getUuid(), entity.getName(), BusinessOwner.mapEntity(entity.getOwner()), mapEntity(entity.getParent()));
	}

	public static BusinessUnitEntity map(BusinessUnit entity) {
		return entity == null ? null : new BusinessUnitEntity(entity.getUuid(), entity.getName(), BusinessOwner.map(entity.getOwner()), map(entity.getParent()));
	}

}
