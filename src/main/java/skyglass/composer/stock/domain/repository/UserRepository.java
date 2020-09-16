package skyglass.composer.stock.domain.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.dto.ExtUserDTO;
import skyglass.composer.stock.domain.dto.UserDTO;
import skyglass.composer.stock.domain.dto.UserDTOFactory;
import skyglass.composer.stock.domain.model.CrudAction;
import skyglass.composer.stock.domain.model.UserHelper;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.UserEntity;
import skyglass.composer.stock.exceptions.BusinessRuleValidationException;
import skyglass.composer.stock.exceptions.ClientException;
import skyglass.composer.stock.exceptions.NotAllowedException;
import skyglass.composer.stock.exceptions.NotNullableNorEmptyException;
import skyglass.composer.utils.PlatformUtil;

/**
 * @author skyglass
 */
@Repository
@Transactional
public class UserRepository extends AEntityBean<UserEntity> {

	@Autowired
	protected PermissionBean permissionBean;

	/**
	 * Convenience method to get the user from the session context.
	 *
	 * @return User based on the principal's name
	 */
	public UserDTO getUserDTOFromCtx() {
		return getUserInfo();
	}

	@NotNull
	public UserDTO getUserInfo() {
		UserEntity user = permissionBean.getUserFromContext();
		if (user != null) {
			ExtUserDTO extUser = getUserFromExt(user.getName());
			if (extUser == null) {
				throw new ClientException(HttpStatus.NOT_FOUND, "Currently logged in user could not be found");
			}

			return UserDTOFactory.createUserDTO(user, extUser);
		}

		throw new ClientException(HttpStatus.NOT_FOUND, "Currently logged in user could not be found");
	}

	public UserEntity findUser(String userNameOrId) {
		if (StringUtils.isBlank(userNameOrId)) {
			return null;
		}

		UserEntity user = findByUuid(userNameOrId);
		if (user == null) {
			user = findByName(userNameOrId);
		}

		return user;
	}

	@Override
	public UserEntity findByUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}

		String queryStr = "SELECT e FROM UserEntity e WHERE e.uuid = :uuid";
		TypedQuery<UserEntity> typedQuery = entityBeanUtil.createQuery(queryStr, UserEntity.class)
				.setParameter("uuid", uuid);

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

	public UserEntity findByName(String name) {
		if (StringUtils.isBlank(name)) {
			return null;
		}

		String queryStr = "SELECT e FROM UserEntity e WHERE LOWER(e.username) = LOWER(:username)";
		TypedQuery<UserEntity> typedQuery = entityBeanUtil.createQuery(queryStr, UserEntity.class)
				.setParameter("username", name);

		return EntityUtil.getSingleResultSafely(typedQuery);
	}

	@NotNull
	public List<UserEntity> findByNames(List<String> names) {
		if (names == null || names.isEmpty()) {
			return null;
		}

		String queryStr = "SELECT u FROM UserEntity u WHERE u.username IN :names";
		TypedQuery<UserEntity> typedQuery = entityBeanUtil.createQuery(queryStr, UserEntity.class);
		typedQuery.setParameter("names", names);
		typedQuery.setParameter("userUuid", permissionBean.getUserFromContext().getUuid());

		return EntityUtil.getListResultSafely(typedQuery);
	}

	@NotNull
	private List<ExtUserDTO> getExtUsersFromResponse(Map<String, Object> resultMap)
			throws IOException, IllegalArgumentException {

		return Collections.emptyList();
	}

	@NotNull
	public ExtUserDTO getUserFromExt(String name) {
		if (StringUtils.isBlank(name)) {
			throw new NotNullableNorEmptyException("Name");
		}

		return PlatformUtil.createDummyUser(name);
	}

	public boolean isCurrentUserAdmin() {
		permissionBean.checkAdmin();
		return true;
	}

	public boolean isCurrentUserAllowedToEditGlobalEntities() {
		return isCurrentUserAdmin();
	}

	public UserDTO getExtUser(UserEntity user) {
		if (user == null) {
			return null;
		}

		ExtUserDTO extUser = getUserFromExt(user.getName());

		UserDTO dto = UserDTOFactory.createUserDTO(user, extUser);

		if (dto == null) {
			throw new ClientException("Could not create user dto");
		}

		return dto;
	}

	@NotNull
	public UserEntity create(ExtUserDTO entity, UserDTO userDTO) {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null.");
		}

		UserEntity user;
		if (entity.getId() == null || entity.getId().isEmpty()) {
			user = createExtUser(entity, userDTO);
		} else {
			user = getExtUser(entity.getId(), userDTO);
		}

		if (user == null) {
			throw new UnsupportedOperationException("Ext user creation failed.");
		}

		return user;
	}
	
	@Override
	public UserEntity update(UserEntity user) {
		return super.update(user);
	}
	
	@Override
	public UserEntity create(UserEntity user) {
		return super.create(user);
	}

	@NotNull
	private UserEntity getExtUser(String extUserId, UserDTO userDTO) {
		if (StringUtils.isBlank(extUserId)) {
			throw new IllegalArgumentException("Ext user ID cannot be null or empty.");
		}

		// check if the Ext user also exists
		getUserFromExt(extUserId);

		return createUser(extUserId, userDTO);
	}

	@NotNull
	private UserEntity createUser(String extUserId, UserDTO userDTO) throws IllegalArgumentException, IllegalStateException {
		if (StringUtils.isBlank(extUserId)) {
			throw new IllegalArgumentException("Ext user ID cannot be null or empty.");
		}

		UserEntity dbUser = permissionBean.findByName(extUserId);
		UserHelper.checkExists(dbUser);
		userDTO.setUsername(extUserId);
		UserEntity user = UserDTOFactory.createUser(dbUser, userDTO);

		try {
			entityBeanUtil.persist(user);
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		}

		return user;
	}

	private UserEntity createExtUser(ExtUserDTO extUser, UserDTO userDTO) {
		if (extUser == null) {
			throw new IllegalArgumentException("Ext user cannot be null");
		}

		if (extUser.getId() == null || extUser.getId().isEmpty()) {
			return null;
		}

		return createUser(extUser.getId(), userDTO);
	}

	@NotNull
	private UserEntity createUser(String username)
			throws IllegalStateException {
		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(UserEntity.class, CrudAction.CREATE);
		}

		UserEntity user = findByName(username);
		if (user == null) {
			user = new UserEntity();
			user.setName(username);

			try {
				user = entityBeanUtil.persist(user);
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			}
		}

		return user;
	}

	@NotNull
	public UserEntity createUser(UserEntity user) {
		if (user == null || StringUtils.isBlank(user.getName())) {
			throw new IllegalArgumentException("User name cannot be null or empty.");
		}

		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(UserEntity.class, CrudAction.CREATE);
		}

		UserEntity dbUser = permissionBean.findByName(user.getName());
		if (dbUser != null) {
			throw new BusinessRuleValidationException(
					String.format("User with such name (%s) already exists", user.getName()));
		}

		entityBeanUtil.persist(user);
		return user;
	}

	@NotNull
	private UserEntity updateUser(String extUserId, UserDTO userDTO)
			throws IllegalArgumentException, IllegalStateException, BusinessRuleValidationException {
		if (StringUtils.isBlank(extUserId) && StringUtils.isBlank(userDTO.getUuid())) {
			throw new IllegalArgumentException("User ID cannot be null");
		}

		UserEntity dbUser = findByName(extUserId);
		if (dbUser == null) {
			dbUser = findByUuid(userDTO.getUuid());
			if (dbUser != null) {
				//the name has been changed
				dbUser.setName(extUserId);
			}
		}
		if (dbUser != null) {

			try {
				entityBeanUtil.merge(dbUser);
			} catch (TransactionRequiredException ex) {
				throw new IllegalStateException(ex);
			}

			return dbUser;
		} else {
			throw new IllegalArgumentException("Could not find user with ID: " + extUserId);
		}
	}

	public void deleteUser(UserEntity user) {
		if (user == null) {
			throw new IllegalArgumentException("User cannot be null");
		}

		if (!isCurrentUserAdmin()) {
			throw new NotAllowedException(user);
		}

		deleteUserAndAssociations(user.getUuid());
	}

	private void deleteUserAndAssociations(String userUuid) {
		UserEntity user = findByUuidSecure(userUuid);
		deleteUserAssociations(userUuid);
		entityBeanUtil.remove(user);
	}

	private void deleteUserAssociations(String userUuid) {

	}

	public UserDTO getUserByExtID(String id) {
		ExtUserDTO extUser = getUserFromExt(id);
		UserEntity user = findByName(extUser.getId());
		if (user == null) {
			return null;
		}

		return UserDTOFactory.createUserDTO(user, extUser);
	}

	/**
	 * Search for a list of users specified by their UUIDs
	 *
	 * @param assigneeUuidList
	 * @return
	 */
	@NotNull
	public List<UserEntity> findByUuids(@NotNull List<String> assigneeUuidList) {
		if (!assigneeUuidList.isEmpty()) {
			TypedQuery<UserEntity> usersQuery = entityBeanUtil.createQuery("SELECT u FROM UserEntity u WHERE u.uuid in :uuidList",
					UserEntity.class);
			usersQuery.setParameter("uuidList", assigneeUuidList);

			return EntityUtil.getListResultSafely(usersQuery);
		}

		return new ArrayList<>();

	}

}
