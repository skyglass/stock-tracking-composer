package skyglass.composer.security.entity.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.entity.model.EntityUtil;

@Repository
@Transactional
public class ContextRepository extends AEntityRepository<ContextEntity> {

	@Autowired
	private ContextHierarchyRepository contextHierarchyRepository;

	public ContextEntity create(ContextEntity entity) {
		ContextEntity result = createEntity(entity);
		contextHierarchyRepository.create(result, result);
		ContextEntity parent = result.getParent();
		while (parent != null) {
			contextHierarchyRepository.create(result, result.getParent());
			parent = parent.getParent();
		}

		return result;
	}

	public List<ContextEntity> find(Context parent) {
		String queryStr = "SELECT ctx FROM ContextEntity ctx WHERE "
				+ (parent == null ? "ctx.parent IS NULL " : "ctx.parent.uuid = :parentUuid ")
				+ "ORDER BY ctx.name";
		TypedQuery<ContextEntity> query = entityBeanUtil.createQuery(queryStr, ContextEntity.class);
		if (parent != null) {
			query.setParameter("parentUuid", parent.getUuid());
		}
		return EntityUtil.getListResultSafely(query);
	}

	public List<ContextEntity> findAll(Context parent) {
		String queryStr = "SELECT ctx FROM ContextEntity ctx WHERE ctx.parent.uuid = :parentUuid ORDER BY ctx.name";
		TypedQuery<ContextEntity> query = entityBeanUtil.createQuery(queryStr, ContextEntity.class);
		query.setParameter("parentUuid", parent.getUuid());
		return EntityUtil.getListResultSafely(query);
	}

}
