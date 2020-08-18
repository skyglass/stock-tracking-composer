package skyglass.composer.stock.persistence;

import org.springframework.data.repository.CrudRepository;

import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;

public interface BusinessUnitRepository extends CrudRepository<BusinessUnitEntity, String> {

	BusinessUnitEntity findByUuid(String uuid);
}
