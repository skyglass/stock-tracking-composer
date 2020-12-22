package skyglass.composer.common.factory;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public abstract class ACopyObjectFactory<A extends AObject, E extends AEntity>
		implements CopyEntityFactory<A, E> {

}
