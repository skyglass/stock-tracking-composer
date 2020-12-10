package skyglass.composer.stock;

import java.util.List;
import java.util.stream.Collectors;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public interface CopyEntityFactory<A extends AObject, E extends AEntity> extends ObjectFactory<A, E> {

	public E copyEntity(E entity);

	public default List<E> copyEntityList(List<E> list) {
		return list.stream().map(p -> copyEntity(p)).collect(Collectors.toList());
	}

	public default List<E> copyObjectList(List<A> list) {
		return list.stream().map(o -> entity(o))
				.map(e -> copyEntity(e)).collect(Collectors.toList());
	}

}
