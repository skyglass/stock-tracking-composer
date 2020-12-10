package skyglass.composer.stock.entity.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.factory.BusinessOwnerFactory;
import skyglass.composer.stock.domain.model.BusinessOwner;
import skyglass.composer.stock.domain.repository.BusinessOwnerRepository;
import skyglass.composer.stock.entity.model.BusinessOwnerEntity;

@Component
class BusinessOwnerServiceImpl implements BusinessOwnerService {

	@Autowired
	private BusinessOwnerRepository businessOwnerRepository;

	@Autowired
	private BusinessOwnerFactory businessOwnerFactory;

	@Override
	public Iterable<BusinessOwner> getAll() {
		return StreamSupport.stream(businessOwnerRepository.findAll().spliterator(), false)
				.map(e -> businessOwnerFactory.object(e))
				.collect(Collectors.toList());
	}

	@Override
	public BusinessOwner getByUuid(String uuid) {
		BusinessOwnerEntity entity = this.businessOwnerRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return businessOwnerFactory.object(entity);
	}

	@Override
	public BusinessOwner create(BusinessOwner businessOwner) {
		return businessOwnerFactory.object(businessOwnerRepository.create(
				businessOwnerFactory.entity(businessOwner)));
	}

}
