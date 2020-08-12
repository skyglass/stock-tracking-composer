package skyglass.composer.stock;

import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.stock.persistence.AEntity;

public interface EntityRepository<E extends AEntity> {

	public E findByUuid(String uuid);

	public E findByUuidSecure(String uuid) throws NotAccessibleException;

}
