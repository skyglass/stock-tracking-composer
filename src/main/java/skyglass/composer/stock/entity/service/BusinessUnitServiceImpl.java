package skyglass.composer.stock.entity.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.factory.BusinessUnitFactory;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.repository.BusinessUnitRepository;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;

@Component
class BusinessUnitServiceImpl implements BusinessUnitService {

	@Autowired
	private BusinessUnitRepository businessUnitRepository;

	@Autowired
	private BusinessUnitFactory businessUnitFactory;

	@Override
	public Iterable<BusinessUnit> getAll() {
		return businessUnitRepository.findAll().stream()
				.map(e -> businessUnitFactory.object(e))
				.collect(Collectors.toList());
	}

	@Override
	public BusinessUnit getByUuid(String uuid) {
		BusinessUnitEntity entity = this.businessUnitRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return businessUnitFactory.object(entity);
	}

	@Override
	public BusinessUnit create(BusinessUnit businessUnit) {
		return businessUnitFactory.object(businessUnitRepository.create(
				businessUnitFactory.entity(businessUnit)));
	}

	@Override
	public List<BusinessUnit> find(BusinessUnit parent) {
		return businessUnitFactory.objectList(businessUnitRepository.find(parent));
	}

	@Override
	public List<BusinessUnit> findAll(BusinessUnit parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
