package skyglass.composer.stock.domain.api;

import skyglass.composer.stock.domain.BusinessUnit;

public interface BusinessUnitService {

	Iterable<BusinessUnit> getAll();

	BusinessUnit getByUuid(String uuid);

}
