package skyglass.composer.common.factory;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public abstract class AObjectParamsFactory<A extends AObject, E extends AEntity>
		implements ObjectParamsFactory<A, E> {

	@Override
	public E createEntity(A object) {
		return createEntityFromParams(object);
	}

}
