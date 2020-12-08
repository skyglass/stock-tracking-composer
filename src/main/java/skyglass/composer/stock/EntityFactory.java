package skyglass.composer.stock;

import java.util.List;
import java.util.stream.Collectors;

import skyglass.composer.stock.domain.model.AObject;
import skyglass.composer.stock.entity.model.AEntity;

public interface EntityFactory<A extends AObject, E extends AEntity> {

	public A createDto(E entity);

	public E createEntity(A dto);

	public default A dto(E entity) {
		return createDto(entity);
	}

	public default E entity(A dto) {
		return createEntity(dto);
	}

	public default List<A> dtoList(List<E> list) {
		return list.stream().map(p -> dto(p)).collect(Collectors.toList());
	}

	public default List<E> entityList(List<A> list) {
		return list.stream().map(p -> entity(p)).collect(Collectors.toList());
	}

}
