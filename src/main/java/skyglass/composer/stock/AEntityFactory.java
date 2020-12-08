package skyglass.composer.stock;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public abstract class AEntityFactory<A extends AObject, E extends AEntity>
		implements EntityFactory<A, E> {

}
