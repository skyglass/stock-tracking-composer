package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.model.BusinessOwner;
import skyglass.composer.stock.domain.repository.BusinessOwnerRepository;
import skyglass.composer.stock.entity.model.BusinessOwnerEntity;

@Component
class BusinessOwnerServiceImpl implements BusinessOwnerService {

	private final BusinessOwnerRepository businessOwnerBean;

	BusinessOwnerServiceImpl(BusinessOwnerRepository businessOwnerBean) {
		this.businessOwnerBean = businessOwnerBean;
	}

	@Override
	public Iterable<BusinessOwner> getAll() {
		return StreamSupport.stream(businessOwnerBean.findAll().spliterator(), false)
				.map(e -> BusinessOwner.mapEntity(e))
				.collect(Collectors.toList());
	}

	@Override
	public BusinessOwner getByUuid(String uuid) {
		BusinessOwnerEntity entity = this.businessOwnerBean.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return BusinessOwner.mapEntity(entity);
	}

}
