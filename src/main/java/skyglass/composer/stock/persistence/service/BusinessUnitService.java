package skyglass.composer.stock.persistence.service;

import skyglass.composer.stock.domain.BusinessUnit;

public interface BusinessUnitService {

	Iterable<BusinessUnit> getAll();

	BusinessUnit getByUuid(String uuid);

}
