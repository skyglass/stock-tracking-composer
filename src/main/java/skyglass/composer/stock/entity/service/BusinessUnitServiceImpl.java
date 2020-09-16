package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.repository.BusinessUnitBean;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;

@Component
class BusinessUnitServiceImpl implements BusinessUnitService {

	private final BusinessUnitBean businessUnitBean;

	BusinessUnitServiceImpl(BusinessUnitBean businessUnitBean) {
		this.businessUnitBean = businessUnitBean;
	}

	@Override
	public Iterable<BusinessUnit> getAll() {
		return StreamSupport.stream(businessUnitBean.findAll().spliterator(), false)
				.map(e -> BusinessUnit.mapEntity(e))
				.collect(Collectors.toList());
	}

	@Override
	public BusinessUnit getByUuid(String uuid) {
		BusinessUnitEntity entity = this.businessUnitBean.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return BusinessUnit.mapEntity(entity);
	}

}
