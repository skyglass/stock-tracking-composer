package skyglass.composer.security.entity.service;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.security.domain.factory.OwnerFactory;
import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.model.OwnerEntity;
import skyglass.composer.security.entity.repository.OwnerRepository;

@Component
class OwnerServiceImpl implements OwnerService {

	@Autowired
	private OwnerRepository ownerRepository;

	@Autowired
	private OwnerFactory ownerFactory;

	@Override
	public Iterable<Owner> getAll() {
		return StreamSupport.stream(ownerRepository.findAll().spliterator(), false)
				.map(e -> ownerFactory.object(e))
				.collect(Collectors.toList());
	}

	@Override
	public Owner getByUuid(String uuid) {
		OwnerEntity entity = this.ownerRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return ownerFactory.object(entity);
	}

	@Override
	public Owner create(Owner owner) {
		return ownerFactory.object(ownerRepository.create(
				ownerFactory.entity(owner)));
	}

}
