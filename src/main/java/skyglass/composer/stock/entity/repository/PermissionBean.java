package skyglass.composer.stock.entity.repository;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.EntityBeanUtil;
import skyglass.composer.stock.domain.model.UserHelper;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.UserEntity;
import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.utils.PlatformUtil;

@Repository
public class PermissionBean implements PermissionApi {

	@Autowired
	private EntityBeanUtil entityBeanUtil;

	@Override
	public String getUsernameFromContext() {
		return PlatformUtil.getUsernameFromCtx();
	}

	@Transactional(readOnly = true)
	@Override
	public UserEntity getUserFromContext() {
		UserEntity currentUser = null;

		String username = getUsernameFromContext();
		if (StringUtils.isNotBlank(username)) {
			currentUser = findByName(username);
		}

		return currentUser;
	}

	@Override
	@Transactional(readOnly = true)
	public UserEntity getUser(String userId) {
		UserEntity user = null;
		if (StringUtils.isBlank(userId)) {
			user = getUserFromContext();
		} else {
			user = findByName(userId);
		}
		if (user == null) {
			throw new NotAccessibleException(UserEntity.class, userId);
		}
		return user;
	}

	@Override
	@Transactional(readOnly = true)
	public void checkAdmin() {
		UserHelper.checkAdmin(getUserFromContext());
	}

	@Override
	public UserEntity findByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		String queryStr = "SELECT e FROM UserEntity e WHERE LOWER(e.username) = LOWER(:username)";
		TypedQuery<UserEntity> typedQuery = entityBeanUtil.createQuery(queryStr, UserEntity.class)
				.setParameter("username", name);

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

}
