package skyglass.composer.stock.entity.service;

import skyglass.composer.stock.domain.model.BusinessOwner;

public interface BusinessOwnerService {

	Iterable<BusinessOwner> getAll();

	BusinessOwner getByUuid(String uuid);

}
