package skyglass.composer.stock.domain;

public interface BusinessUnitService {

	Iterable<BusinessUnit> getAll();

	BusinessUnit getByUuid(String uuid);

}
