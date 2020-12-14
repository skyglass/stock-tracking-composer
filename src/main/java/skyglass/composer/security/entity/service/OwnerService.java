package skyglass.composer.security.entity.service;

import skyglass.composer.security.domain.model.Owner;

public interface OwnerService {

	Iterable<Owner> getAll();

	Owner getByUuid(String uuid);

	Owner create(Owner owner);

}
