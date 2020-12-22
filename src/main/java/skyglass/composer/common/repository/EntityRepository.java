package skyglass.composer.common.repository;

import skyglass.composer.stock.entity.model.AEntity;
import skyglass.composer.stock.exceptions.NotAccessibleException;

public interface EntityRepository<E extends AEntity> {

	public E findByUuid(String uuid);

	public E findByUuidSecure(String uuid) throws NotAccessibleException;

}
