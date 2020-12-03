package skyglass.composer.stock.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.BusinessOwnerEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BusinessOwner extends AObject {

	private static final long serialVersionUID = -4855746732917542351L;

	private String uuid;

	private String name;

	public static BusinessOwner mapEntity(BusinessOwnerEntity entity) {
		return new BusinessOwner(entity.getUuid(), entity.getName());

	}

	public static BusinessOwnerEntity map(BusinessOwner entity) {
		return new BusinessOwnerEntity(entity.getUuid(), entity.getName());

	}

}
