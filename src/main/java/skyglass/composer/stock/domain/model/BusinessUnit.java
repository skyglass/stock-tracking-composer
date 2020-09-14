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

	public static BusinessUnit mapEntity(BusinessUnitEntity entity) {
		return new BusinessUnit(entity.getUuid(), entity.getName());

	}

	public static BusinessUnitEntity map(BusinessUnit entity) {
		return new BusinessUnitEntity(entity.getUuid(), entity.getName());

	}

}
