package skyglass.composer.stock;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.model.IDeletable;
import skyglass.composer.stock.entity.model.AEntity;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.exceptions.AlreadyExistsException;
import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.stock.exceptions.NotNullableNorEmptyException;
import skyglass.composer.stock.exceptions.PermissionDeniedException;

@Transactional
public abstract class AEntityBean<E extends AEntity> implements EntityRepository<E> {

	@Autowired
	protected EntityBeanUtil entityBeanUtil;

	protected final Class<E> persistentClass;

	@SuppressWarnings("unchecked")
	public AEntityBean() {
		persistentClass = (Class<E>) GenericTypeResolver.resolveTypeArgument(getClass(), AEntityBean.class);
		if (persistentClass == null) {
			throw new IllegalStateException("persistentClass cannot be null");
		}
	}

	public void checkAccessibility(String uuid) throws NotAccessibleException {
		findByUuidSecure(uuid);
	}

	@NotNull
	@Override
	public E findByUuidSecure(String uuid) throws NotAccessibleException {
		if (StringUtils.isBlank(uuid)) {
			throw new NotNullableNorEmptyException(persistentClass.getSimpleName() + " UUID");
		}

		E result = findByUuid(uuid);
		if (result == null) {
			throw new PermissionDeniedException(persistentClass, uuid);
		}

		return result;
	}

	@NotNull
	public E findNullableByUuidSecure(String uuid) throws NotAccessibleException {
		if (StringUtils.isNotBlank(uuid)) {
			E result = findByUuid(uuid);
			if (result == null) {
				//check that entity actually exists in the database
				result = entityBeanUtil.find(persistentClass, uuid);
				if (result != null) {
					throw new NotAccessibleException(persistentClass, uuid);
				}
			}
			return result;
		}
		return null;
	}

	@Override
	public E findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		return entityBeanUtil.find(persistentClass, uuid);
	}
	
	@NotNull
	public Collection<E> findAll() {
		CriteriaBuilder criteriaBuilder = entityBeanUtil.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(persistentClass);
		Root<E> from = criteriaQuery.from(persistentClass);
		CriteriaQuery<E> select = criteriaQuery.select(from);
		TypedQuery<E> typedQuery = entityBeanUtil.createQuery(select);

		List<E> list = typedQuery.getResultList();
		if (list == null) {
			list = Collections.emptyList();
		}

		return list;
	}

	@NotNull
	public Collection<E> findPaginated(int offset, int limit) {
		CriteriaBuilder criteriaBuilder = entityBeanUtil.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder.createQuery(persistentClass);
		Root<E> from = criteriaQuery.from(persistentClass);
		CriteriaQuery<E> select = criteriaQuery.select(from);
		TypedQuery<E> typedQuery = entityBeanUtil.createQuery(select);

		return findPaginated(typedQuery, offset, limit);
	}

	@NotNull
	protected <T extends AEntity> Collection<T> findPaginated(TypedQuery<T> typedQuery, int offset, int limit) {
		if (typedQuery != null) {
			if (offset >= 0) {
				typedQuery.setFirstResult(offset);
			}

			typedQuery.setMaxResults(limit <= 0 ? EntityUtil.DEFAULT_PAGINATED_MAX_RESULTS : limit);

			List<T> list = typedQuery.getResultList();
			if (list == null) {
				list = Collections.emptyList();
			}

			return list;
		}

		return Collections.emptyList();
	}

	@NotNull
	protected E create(E entity)
			throws AlreadyExistsException, IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		return persist(entity);
	}

	@NotNull
	protected E persist(@NotNull E entity)
			throws AlreadyExistsException, IllegalArgumentException, IllegalStateException {
		try {
			entityBeanUtil.persist(entity);
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		} catch (EntityExistsException ex) {
			throw new AlreadyExistsException(entity, ex);
		}

		return entity;
	}

	@NotNull
	protected E update(E entity) throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		String uuid = entity.getUuid();
		if (StringUtils.isBlank(uuid)) {
			throw new IllegalStateException("Entity without UUID cannot be updated");
		}

		return merge(entity);
	}

	protected E merge(@NotNull E entity) throws IllegalArgumentException, IllegalStateException {
		try {

			return entityBeanUtil.merge(entity);
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		}
	}

	@NotNull
	protected E preDeleteUuid(@NotNull String uuid)
			throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		E entity = findByUuid(uuid);
		if (entity == null) {
			throw new IllegalArgumentException("Could not delete " + persistentClass.getSimpleName() + " with UUID '"
					+ uuid + "', entity not accessible with that UUID");
		}

		return entity;
	}

	@NotNull
	protected E delete(String uuid) throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (StringUtils.isBlank(uuid)) {
			throw new IllegalArgumentException("UUID cannot neither be null nor empty");
		}

		return remove(preDeleteUuid(uuid));
	}

	protected void delete(E entity) throws IllegalArgumentException, IllegalStateException, NotAccessibleException {
		if (entity == null) {
			throw new IllegalArgumentException("Entity cannot be null");
		}

		remove(entity);
	}

	protected E remove(@NotNull E entity) throws IllegalArgumentException, IllegalStateException {
		try {
			if (entity instanceof IDeletable) {
				((IDeletable) entity).setDeleted(true);
				entity = merge(entity);
			} else {
				entityBeanUtil.remove(entity);
			}
		} catch (TransactionRequiredException ex) {
			throw new IllegalStateException(ex);
		}

		return entity;
	}

	protected void detachEntity(@NotNull E entity) {
		entityBeanUtil.detach(entity);
	}

}
