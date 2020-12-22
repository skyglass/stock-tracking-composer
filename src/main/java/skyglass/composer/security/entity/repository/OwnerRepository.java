package skyglass.composer.security.entity.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.security.entity.model.OwnerEntity;

@Repository
@Transactional
public class OwnerRepository extends AEntityRepository<OwnerEntity> {

	public OwnerEntity create(OwnerEntity entity) {
		OwnerEntity result = createEntity(entity);
		return result;
	}

}
