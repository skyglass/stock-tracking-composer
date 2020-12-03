package skyglass.composer.stock.entity.service;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.repository.BusinessUnitRepository;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;

@Component
class BusinessUnitServiceImpl implements BusinessUnitService {

	private final BusinessUnitRepository businessUnitRepository;

	BusinessUnitServiceImpl(BusinessUnitRepository businessUnitBean) {
		this.businessUnitRepository = businessUnitBean;
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

	@Override
	public BusinessUnit create(BusinessUnit businessUnit) {
		return BusinessUnit.mapEntity(businessUnitRepository.create(BusinessUnit.map(businessUnit)));
	}

	@Override
	public Collection<BusinessUnit> find(BusinessUnit parent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<BusinessUnit> findAll(BusinessUnit parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
