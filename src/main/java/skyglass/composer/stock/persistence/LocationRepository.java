package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

interface LocationRepository extends CrudRepository<LocationEntity, String> {
	LocationEntity findByUuid(String uuid);
}
