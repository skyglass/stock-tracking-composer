package skyglass.composer.common.factory;

import java.util.List;
import java.util.stream.Collectors;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public interface ObjectFactory<A extends AObject, E extends AEntity> {

	public A createObject(E entity);

	public E createEntity(A object);

	public default A object(E entity) {
		return createObject(entity);
	}

	public default E entity(A dto) {
		return createEntity(dto);
	}

	public default List<A> objectList(List<E> list) {
		return list.stream().map(p -> object(p)).collect(Collectors.toList());
	}

	public default List<E> entityList(List<A> list) {
		return list.stream().map(p -> entity(p)).collect(Collectors.toList());
	}

}
