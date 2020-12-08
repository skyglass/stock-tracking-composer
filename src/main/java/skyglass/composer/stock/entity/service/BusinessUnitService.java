package skyglass.composer.stock.entity.service;

import java.util.List;

import skyglass.composer.stock.domain.model.BusinessUnit;

public interface BusinessUnitService {

	Iterable<BusinessUnit> getAll();

	BusinessUnit getByUuid(String uuid);

	BusinessUnit create(BusinessUnit businessUnit);

	List<BusinessUnit> find(BusinessUnit parent);

	List<BusinessUnit> findAll(BusinessUnit parent);

}
