package skyglass.composer.common.factory;

import java.util.List;
import java.util.stream.Collectors;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public interface ObjectParamsFactory<A extends AObject, E extends AEntity>
		extends ObjectFactory<A, E> {

	public E createEntityFromParams(A object, Object... params);

	public default E entityFromParams(A dto, Object... params) {
		return createEntityFromParams(dto, params);
	}

	public default List<E> entityListFromParams(List<A> list, Object... params) {
		return list.stream().map(p -> entityFromParams(p, params)).collect(Collectors.toList());
	}

}
