package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.model.BusinessUnit;

public interface BusinessUnitService {

	Iterable<BusinessUnit> getAll();

	BusinessUnit getByUuid(String uuid);

}
