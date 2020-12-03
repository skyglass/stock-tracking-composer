package skyglass.composer.stock.entity.service;

import java.util.Collection;

import skyglass.composer.stock.domain.model.BusinessUnit;

public interface BusinessUnitService {

	Iterable<BusinessUnit> getAll();

	BusinessUnit getByUuid(String uuid);

	BusinessUnit create(BusinessUnit businessUnit);

	Collection<BusinessUnit> find(BusinessUnit parent);

	Collection<BusinessUnit> findAll(BusinessUnit parent);

}
