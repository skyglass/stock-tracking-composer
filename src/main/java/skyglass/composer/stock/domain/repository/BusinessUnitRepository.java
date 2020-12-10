package skyglass.composer.stock.domain.repository;

import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.EntityUtil;

@Repository
@Transactional
public class BusinessUnitRepository extends AEntityRepository<BusinessUnitEntity> {

	@Autowired
	private BusinessUnitHierarchyRepository businessUnitHierarchyRepository;

	public BusinessUnitEntity create(BusinessUnitEntity entity) {
		BusinessUnitEntity result = createEntity(entity);
		businessUnitHierarchyRepository.create(result, result);
		BusinessUnitEntity parent = result.getParent();
		while (parent != null) {
			businessUnitHierarchyRepository.create(result, result.getParent());
			parent = parent.getParent();
		}

		return result;
	}

	public List<BusinessUnitEntity> find(BusinessUnit parent) {
		String queryStr = "SELECT bu FROM BusinessUnitEntity bu WHERE "
				+ (parent == null ? "bu.parent IS NULL " : "bu.parent.uuid = :parentUuid ")
				+ "ORDER BY bu.name";
		TypedQuery<BusinessUnitEntity> query = entityBeanUtil.createQuery(queryStr, BusinessUnitEntity.class);
		if (parent != null) {
			query.setParameter("parentUuid", parent.getUuid());
		}
		return EntityUtil.getListResultSafely(query);
	}

	public List<BusinessUnitEntity> findAll(BusinessUnit parent) {
		String queryStr = "SELECT bu FROM BusinessUnitEntity bu WHERE bu.parent.uuid = :parentUuid ORDER BY bu.name";
		TypedQuery<BusinessUnitEntity> query = entityBeanUtil.createQuery(queryStr, BusinessUnitEntity.class);
		query.setParameter("parentUuid", parent.getUuid());
		return EntityUtil.getListResultSafely(query);
	}

}
