package skyglass.composer.stock.domain.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.entity.model.BusinessOwnerEntity;

@Repository
@Transactional
public class BusinessOwnerRepository extends AEntityRepository<BusinessOwnerEntity> {

	public BusinessOwnerEntity create(BusinessOwnerEntity entity) {
		BusinessOwnerEntity result = createEntity(entity);
		return result;
	}

}
