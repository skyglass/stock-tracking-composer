package skyglass.composer.common.factory;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public abstract class AObjectFactory<A extends AObject, E extends AEntity>
		implements ObjectFactory<A, E> {

}
