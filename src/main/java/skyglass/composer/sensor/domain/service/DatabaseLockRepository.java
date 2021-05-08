package skyglass.composer.sensor.domain.service;

import javax.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.EntityBeanUtil;
import skyglass.composer.stock.entity.model.EntityUtil;

@Repository
@Transactional
public class DatabaseLockRepository {

	private static final String INSERT_SQL = "insert into lockmanager values(?object, ?owner)";

	private static final String DELETE_SQL = "delete from lockmanager where object_uuid = ?object and owner_uuid = ?owner";

	private static final String CHECK_SQL = "select object_uuid from lockmanager where object_uuid = ?object and owner_uuid = ?owner";

	@Autowired
	private EntityBeanUtil entityBeanUtil;

	public Object checkLock(String object, String owner) {
		Query executeQuery = entityBeanUtil.createNativeQuery(CHECK_SQL);
		executeQuery.setParameter("object", object);
		executeQuery.setParameter("owner", owner);

		return EntityUtil.getSingleResultSafely(executeQuery);
	}

	public void insertLock(String object, String owner) {
		Query executeQuery = entityBeanUtil.createNativeQuery(INSERT_SQL);
		executeQuery.setParameter("object", object);
		executeQuery.setParameter("owner", owner);
		executeQuery.executeUpdate();
	}

	public void deleteLock(String object, String owner) {
		Query executeQuery = entityBeanUtil.createNativeQuery(DELETE_SQL);
		executeQuery.setParameter("object", object);
		executeQuery.setParameter("owner", owner);
		executeQuery.executeUpdate();
	}

}
