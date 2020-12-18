package skyglass.composer.security.entity.repository;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.utils.AssertUtil;

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

	public void delete(ContextEntity entity) {

		Integer level = null;

		do {
			String queryStr = "SELECT MAX(ctx.level) FROM ContextHierarchy ctxh "
					+ "JOIN Context ctx ON ctx.uuid = ctxh.child_uuid "
					+ "AND ctxh.parent_uuid = :parentUuid";

			Query levelQuery = entityBeanUtil.createNativeQuery(queryStr);
			levelQuery.setParameter("parentUuid", entity.getUuid());
			level = (Integer) EntityUtil.getSingleResultSafely(levelQuery);

			if (level > 0) {
				queryStr = "SELECT ctx.uuid FROM ContextHierarchy ctxh "
						+ "JOIN Context ctx ON ctx.uuid = ctxh.child_uuid "
						+ "AND ctx.level = :level AND ctxh.parent_uuid = :parentUuid";

				Query uuidQuery = entityBeanUtil.createNativeQuery(queryStr);
				uuidQuery.setParameter("level", level);
				uuidQuery.setParameter("parentUuid", entity.getUuid());

				@SuppressWarnings("unchecked")
				List<String> uuids = (List<String>) EntityUtil.getListResultSafely(uuidQuery);

				queryStr = "DELETE FROM ContextHierarchyEntity ctx WHERE ctx.child.uuid IN :uuids";
				Query deleteQuery = entityBeanUtil.createQuery(queryStr);
				deleteQuery.setParameter("uuids", uuids);
				deleteQuery.executeUpdate();

				queryStr = "DELETE FROM ContextEntity ctx WHERE ctx.uuid IN :uuids";
				deleteQuery = entityBeanUtil.createQuery(queryStr);
				deleteQuery.setParameter("uuids", uuids);
				deleteQuery.executeUpdate();

			}
		} while (level > 0);

		String queryStr = "DELETE FROM ContextHierarchyEntity ctx WHERE ctx.child.uuid = :parentUuid";
		Query deleteQuery = entityBeanUtil.createQuery(queryStr);
		deleteQuery.setParameter("parentUuid", entity.getUuid());
		deleteQuery.executeUpdate();

		queryStr = "DELETE FROM ContextEntity ctx WHERE ctx.uuid = :parentUuid";
		deleteQuery = entityBeanUtil.createQuery(queryStr);
		deleteQuery.setParameter("parentUuid", entity.getUuid());
		deleteQuery.executeUpdate();

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
		String queryStr = "SELECT "
				+ (parent == null ? "DISTINCT " : "")
				+ "ctx.child "
				+ "FROM ContextHierarchyEntity ctx WHERE "
				+ (parent == null ? "ctx.parent IS NULL " : "ctx.parent.uuid = :parentUuid AND ctx.child.uuid != ctx.parent.uuid ")
				+ "ORDER BY ctx.child.name";
		TypedQuery<ContextEntity> query = entityBeanUtil.createQuery(queryStr, ContextEntity.class);
		if (parent != null) {
			query.setParameter("parentUuid", parent.getUuid());
		}
		return EntityUtil.getListResultSafely(query);
	}

	public ContextEntity findByName(Context parent, String name) {
		AssertUtil.notEmpty("name", name);
		String queryStr = "SELECT ctx FROM ContextEntity ctx WHERE "
				+ (parent == null ? "ctx.parent IS NULL " : "ctx.parent.uuid = :parentUuid ")
				+ "AND ctx.name = :name "
				+ "ORDER BY ctx.name";
		TypedQuery<ContextEntity> query = entityBeanUtil.createQuery(queryStr, ContextEntity.class);
		if (parent != null) {
			query.setParameter("parentUuid", parent.getUuid());
		}
		query.setParameter("name", name);
		return EntityUtil.getSingleResultSafely(query);
	}

}
