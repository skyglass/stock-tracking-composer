package skyglass.composer.security.entity.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.security.entity.model.ContextHierarchyEntity;

@Repository
@Transactional
public class ContextHierarchyRepository extends AEntityRepository<ContextHierarchyEntity> {

	public ContextHierarchyEntity create(ContextEntity child, ContextEntity parent) {
		ContextHierarchyEntity result = createEntity(new ContextHierarchyEntity(null, child, parent));

		return result;
	}

}
