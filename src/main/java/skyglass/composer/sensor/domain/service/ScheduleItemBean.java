package skyglass.composer.sensor.domain.service;

import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.sensor.domain.model.ScheduleItem;
import skyglass.composer.stock.entity.model.EntityUtil;

@Repository
@Transactional
public class ScheduleItemBean extends AEntityRepository<ScheduleItem> {

	public List<ScheduleItem> getAllScheduleItems(String machineUuid) {
		TypedQuery<ScheduleItem> query = scheduleItemQuery(machineUuid);
		return EntityUtil.getListResultSafely(query);
	}

	public List<ScheduleItem> getAllScheduleItemsWithinTimePeriod(String machineUuid, Date fromDate, Date toDate) {
		String str = getDefaultQuery() + " AND (i.validTo > :fromDate AND i.validFrom < :toDate)";
		TypedQuery<ScheduleItem> query = entityBeanUtil.createQuery(str, ScheduleItem.class);
		query.setParameter("fromDate", fromDate);
		query.setParameter("toDate", toDate);
		query.setParameter("machineUuid", machineUuid);
		return EntityUtil.getListResultSafely(query);
	}

	private TypedQuery<ScheduleItem> scheduleItemQuery(String machineUuid) {
		TypedQuery<ScheduleItem> query = entityBeanUtil.createQuery(getDefaultQuery(), ScheduleItem.class);
		query.setParameter("machineUuid", machineUuid);
		return query;
	}

	private String getDefaultQuery() {
		String query = "SELECT DISTINCT(i) FROM ScheduleItem i WHERE i.machine.uuid = :machineUuid";
		return query;
	}

}
