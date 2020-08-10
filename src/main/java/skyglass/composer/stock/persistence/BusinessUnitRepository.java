package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

interface BusinessUnitRepository extends CrudRepository<BusinessUnitEntity, String> {

	BusinessUnitEntity findByUuid(String uuid);
}
