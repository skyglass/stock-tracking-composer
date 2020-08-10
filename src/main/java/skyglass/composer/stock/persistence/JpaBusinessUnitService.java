package skyglass.composer.stock.persistence;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.BusinessUnit;
import skyglass.composer.stock.BusinessUnitService;

@Component
class JpaBusinessUnitService implements BusinessUnitService {

	private final BusinessUnitRepository businessUnitRepository;

	@PersistenceContext
	private EntityManager entityManager;

	JpaBusinessUnitService(BusinessUnitRepository businessUnitRepository) {
		this.businessUnitRepository = businessUnitRepository;
	}

	@Override
	public Iterable<BusinessUnit> getAll() {
		return StreamSupport.stream(businessUnitRepository.findAll().spliterator(), false)
				.map(this::mapEntity)
				.collect(Collectors.toList());
	}

	@Override
	public BusinessUnit getByUuid(String uuid) {
		BusinessUnitEntity entity = this.businessUnitRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return mapEntity(entity);
	}

	BusinessUnit mapEntity(BusinessUnitEntity entity) {
		return new BusinessUnit(entity.getUuid(), entity.getName());

	}

	BusinessUnitEntity map(BusinessUnit entity) {
		return new BusinessUnitEntity(entity.getUuid(), entity.getName());

	}

}
