package skyglass.composer.security.entity.repository;

import java.util.List;

import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.utils.AssertUtil;

@Repository
@Transactional
public class ContextRepository extends AEntityRepository<ContextEntity> {

	@Autowired
	private ContextHierarchyRepository contextHierarchyRepository;

	public List<ContextEntity> createList(List<ContextEntity> entityList) {
		entityList.forEach(e -> create(e));
		return entityList;
	}

	public ContextEntity create(ContextEntity entity) {
		boolean isNew = StringUtils.isBlank(entity.getUuid());
		ContextEntity result = createOrUpdateEntity(entity);
		if (isNew) {
			contextHierarchyRepository.create(result, result);
			ContextEntity parent = result.getParent();
			while (parent != null) {
				contextHierarchyRepository.create(result, parent);
				parent = parent.getParent();
			}
		}
		return result;
	}

	public void delete(String contextUuid) {
		ContextEntity entity = findByUuidSecure(contextUuid);

		Integer level = null;

		do {
			String queryStr = "SELECT MAX(ctx.level) FROM ContextHierarchy ctxh "
					+ "JOIN Context ctx ON ctx.uuid = ctxh.child_uuid "
					+ "AND ctxh.parent_uuid = :parentUuid";

			Query levelQuery = entityBeanUtil.createNativeQuery(queryStr);
			levelQuery.setParameter("parentUuid", entity.getUuid());
			level = (Integer) EntityUtil.getSingleResultSafely(levelQuery);

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

		} while (level > entity.getLevel());

	}

	public List<ContextEntity> find(String parentUuid) {
		ContextEntity parent = StringUtils.isBlank(parentUuid) ? null : findByUuidSecure(parentUuid);
		String queryStr = "SELECT ctx FROM ContextEntity ctx WHERE "
				+ (parent == null ? "ctx.parent IS NULL " : "ctx.parent.uuid = :parentUuid ")
				+ "ORDER BY ctx.name";
		TypedQuery<ContextEntity> query = entityBeanUtil.createQuery(queryStr, ContextEntity.class);
		if (parent != null) {
			query.setParameter("parentUuid", parent.getUuid());
		}
		return EntityUtil.getListResultSafely(query);
	}

	public List<ContextEntity> findAll(String parentUuid) {
		ContextEntity parent = StringUtils.isBlank(parentUuid) ? null : findByUuidSecure(parentUuid);
		String queryStr = "SELECT "
				+ (parent == null ? "DISTINCT " : "")
				+ "ctx "
				+ "FROM ContextHierarchyEntity ctxh JOIN ContextEntity ctx ON ctx.uuid = ctxh.child.uuid AND "
				+ (parent == null ? "ctx.parent IS NULL " : "ctxh.parent.uuid = :parentUuid AND ctxh.child.uuid != ctxh.parent.uuid ")
				+ "ORDER BY ctx.name";
		TypedQuery<ContextEntity> query = entityBeanUtil.createQuery(queryStr, ContextEntity.class);
		if (parent != null) {
			query.setParameter("parentUuid", parent.getUuid());
		}
		return EntityUtil.getListResultSafely(query);
	}

	public ContextEntity findByName(String parentUuid, String name) {
		ContextEntity parent = StringUtils.isBlank(parentUuid) ? null : findByUuidSecure(parentUuid);
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
