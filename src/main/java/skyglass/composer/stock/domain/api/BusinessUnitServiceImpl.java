package skyglass.composer.stock.domain.api;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.persistence.BusinessUnitRepository;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;

@Component
class BusinessUnitServiceImpl implements BusinessUnitService {

	private final BusinessUnitRepository businessUnitRepository;

	@PersistenceContext
	private EntityManager entityManager;

	BusinessUnitServiceImpl(BusinessUnitRepository businessUnitRepository) {
		this.businessUnitRepository = businessUnitRepository;
	}

	@Override
	public Iterable<BusinessUnit> getAll() {
		return StreamSupport.stream(businessUnitRepository.findAll().spliterator(), false)
				.map(e -> BusinessUnit.mapEntity(e))
				.collect(Collectors.toList());
	}

	@Override
	public BusinessUnit getByUuid(String uuid) {
		BusinessUnitEntity entity = this.businessUnitRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return BusinessUnit.mapEntity(entity);
	}

}
