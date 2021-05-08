package skyglass.composer.sensor.domain.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.sensor.domain.model.Device;
import skyglass.composer.sensor.domain.model.ScheduleItem;
import skyglass.composer.sensor.domain.model.Sensor;
import skyglass.composer.sensor.domain.model.SensorValueDTO;
import skyglass.composer.sensor.domain.model.SensorWarningHistory;
import skyglass.composer.sensor.domain.model.WarningType;
import skyglass.composer.stock.domain.model.CrudAction;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.exceptions.NotAllowedException;
import skyglass.composer.utils.date.DatePeriod;

@Repository
@Transactional
public class SensorWarningHistoryBean extends AEntityRepository<SensorWarningHistory> {

	@Autowired
	private ScheduleItemBean machineScheduleItemBean;

	@Override
	public Collection<SensorWarningHistory> findAll() {
		throw new NotAllowedException(SensorWarningHistory.class, CrudAction.READ);
	}

	@Override
	public SensorWarningHistory findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		String queryStr = getSensorWarningHistoryByUuidQuery();
		TypedQuery<SensorWarningHistory> query = entityBeanUtil.createQuery(queryStr, SensorWarningHistory.class);
		query.setParameter("uuid", uuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	@NotNull
	public List<SensorWarningHistory> findForMachine(Device machine) {
		TypedQuery<SensorWarningHistory> query = findForMachineQuery(machine);
		return EntityUtil.getListResultSafely(query);
	}

	public SensorWarningHistory findLatest(Device machine) {
		TypedQuery<SensorWarningHistory> query = findForMachineQuery(machine);
		query.setMaxResults(1);

		return EntityUtil.getSingleResultSafely(query);
	}

	private TypedQuery<SensorWarningHistory> findForMachineQuery(Device machine) {
		String queryStr = "SELECT swh FROM SensorWarningHistory swh WHERE swh.machine.uuid = :machineUuid ORDER BY swh.startDate DESC";
		TypedQuery<SensorWarningHistory> query = entityBeanUtil.createQuery(queryStr, SensorWarningHistory.class);
		query.setParameter("machineUuid", machine.getUuid());
		return query;
	}

	public SensorWarningHistory findValidForMachine(Device machine, Date validityDate) {
		List<SensorWarningHistory> result = findValidPreviousListForMachine(machine, validityDate);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	@NotNull
	private List<SensorWarningHistory> findValidPreviousListForMachine(Device machine, Date validityDate) {
		String queryStr = "SELECT swh FROM SensorWarningHistory swh WHERE swh.machine.uuid = :machineUuid AND swh.startDate <= :validityDate AND (swh.endDate IS NULL OR swh.endDate > :validityDate) ORDER BY swh.startDate";
		TypedQuery<SensorWarningHistory> query = entityBeanUtil.createQuery(queryStr, SensorWarningHistory.class);
		query.setParameter("machineUuid", machine.getUuid());
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	private List<SensorWarningHistory> findValidNextListForMachine(Device machine, Date validityDate) {
		String queryStr = "SELECT swh FROM SensorWarningHistory swh WHERE swh.machine.uuid = :machineUuid AND swh.startDate > :validityDate ORDER BY swh.startDate";
		TypedQuery<SensorWarningHistory> query = entityBeanUtil.createQuery(queryStr, SensorWarningHistory.class);
		query.setParameter("machineUuid", machine.getUuid());
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	public List<SensorWarningHistory> findValidListForMachineAndPeriod(String machineUuid, Date startDate, Date endDate, boolean desc) {
		String queryStr = "SELECT swh FROM SensorWarningHistory swh WHERE swh.machine.uuid = :machineUuid "
				+ (endDate == null ? "" : "AND swh.startDate < :endDate ")
				+ (startDate == null ? "" : "AND (swh.endDate IS NULL OR swh.endDate > :startDate) ")
				+ "ORDER BY swh.startDate " + (desc ? "DESC" : "ASC");
		TypedQuery<SensorWarningHistory> query = entityBeanUtil.createQuery(queryStr, SensorWarningHistory.class);
		query.setParameter("machineUuid", machineUuid);
		if (startDate != null) {
			query.setParameter("startDate", startDate);
		}
		if (endDate != null) {
			query.setParameter("endDate", endDate);
		}
		return EntityUtil.getListResultSafely(query);
	}

	public SensorWarningHistory createHistoryForSensorValueDTO(SensorValueDTO sensorValue) {
		Sensor sensor = entityBeanUtil.find(Sensor.class, sensorValue.getSensorUuid());
		if (sensor == null) {
			return null;
		}
		return createHistoryForSensorValue(sensor, sensorValue.getCreatedAt(), sensorValue.getValue());
	}

	private SensorWarningHistory createHistoryForSensorValue(Sensor sensor, Date validityDate, double doubleValue) {
		Device machine = sensor.getMachine();
		WarningType warningType = null;
		if (SensorValueBean.SENSORID_RED.equals(sensor.getSensorId())) {
			warningType = WarningType.Red;
		}
		if (SensorValueBean.SENSORID_YELLOW.equals(sensor.getSensorId())) {
			warningType = WarningType.Yellow;
		}
		if (SensorValueBean.SENSORID_GREEN.equals(sensor.getSensorId())) {
			warningType = WarningType.Green;
		}
		if (warningType == null) {
			return null;
		}

		int value = MachineBean.getWarningLightStatusValue(sensor, doubleValue);

		List<SensorWarningHistory> previousList = findValidPreviousListForMachine(machine, validityDate);
		SensorWarningHistory previous = null;
		if (CollectionUtils.isNotEmpty(previousList)) {
			if (previousList.size() > 1) {
				for (int i = 1; i < previousList.size(); i++) {
					entityBeanUtil.remove(previousList.get(i));
				}
			}
			previous = previousList.get(0);
		}

		SensorWarningHistory valid = new SensorWarningHistory();
		valid.setMachine(machine);
		valid.setStartDate(validityDate);

		if (warningType == WarningType.Red) {
			valid.setRedValue(value);
			valid.setYellowValue(previous == null ? -1 : previous.getYellowValue());
			valid.setGreenValue(previous == null ? -1 : previous.getGreenValue());
		}
		if (warningType == WarningType.Yellow) {
			valid.setYellowValue(value);
			valid.setRedValue(previous == null ? -1 : previous.getRedValue());
			valid.setGreenValue(previous == null ? -1 : previous.getGreenValue());
		}
		if (warningType == WarningType.Green) {
			valid.setGreenValue(value);
			valid.setRedValue(previous == null ? -1 : previous.getRedValue());
			valid.setYellowValue(previous == null ? -1 : previous.getYellowValue());
		}

		//duplicate detected, skip the processing to save time
		if (previous != null && previous.getStartDate().equals(validityDate) && previous.getGreenValue() == valid.getGreenValue()
				&& previous.getYellowValue() == valid.getYellowValue() && previous.getRedValue() == valid.getRedValue()) {
			return valid;
		}

		List<ScheduleItem> items = machineScheduleItemBean.getAllScheduleItemsWithinTimePeriod(machine.getUuid(), validityDate, validityDate);

		if (previous != null) {
			valid.setEndDate(previous.getEndDate());
			previous.setEndDate(validityDate);
			//if previous start date equals end date, then the new sensor value warning history interval overrides previous interval. Therefore, previous interval should be deleted.
			//It doesn't make sense to keep interval with the same start and end date in the history anyway
			if (previous.getStartDate().equals(previous.getEndDate())) {
				entityBeanUtil.remove(previous);
			} else {
				setGreyValue(previous, items);
				entityBeanUtil.merge(previous);
			}
		}

		valid.setWarningType(warningType);

		List<SensorWarningHistory> nextList = findValidNextListForMachine(machine, validityDate);

		if (CollectionUtils.isNotEmpty(nextList)) {
			valid.setEndDate(nextList.get(0).getStartDate());

			for (SensorWarningHistory next : nextList) {
				if (next.getWarningType() != warningType) {
					if (warningType == WarningType.Red) {
						next.setRedValue(valid.getRedValue());
					}
					if (warningType == WarningType.Yellow) {
						next.setYellowValue(valid.getYellowValue());
					}
					if (warningType == WarningType.Green) {
						next.setGreenValue(valid.getGreenValue());
					}
					setGreyValue(next, items);
					//if next start date becomes equal to end date, then the next splitted sensor value warning history interval overrides this interval. Therefore, this interval should be deleted
					//It doesn't make sense to keep interval with the same start and end date in the history anyway
					if (next.getStartDate().equals(next.getEndDate())) {
						entityBeanUtil.remove(next);
					} else {
						entityBeanUtil.merge(next);
					}
				} else {
					break;
				}
			}

		}

		setGreyValue(valid, items);

		//if new start date equals end date, then the next sensor value warning history interval overrides new interval. Therefore, new interval should not be created
		//It doesn't make sense to keep interval with the same start and end date in the history anyway
		if (!valid.getStartDate().equals(valid.getEndDate())) {
			return super.createEntity(valid);
		}
		return valid;
	}

	private String getSensorWarningHistoryByUuidQuery() {
		String queryStr = "SELECT DISTINCT(swh) FROM SensorWarningHistory swh JOIN swh.machine m ON swh.machine.uuid = m.uuid";
		return queryStr;
	}

	private void setGreyValue(SensorWarningHistory valid, List<ScheduleItem> items) {
		if (isGreyValue(valid)) {
			boolean isLast = valid.getEndDate() == null;
			Date validEndDate = isLast ? valid.getStartDate() : valid.getEndDate();

			Pair<Boolean, List<DatePeriod>> result = ScheduleItemHelper.isDateWithinTheSchedule(valid.getStartDate(), validEndDate, items);
			if (result.getLeft()) {
				valid.setGreyValue(1);
			} else {
				valid.setGreyValue(0);
			}

			if (!isLast) {
				splitHistoryValue(valid, result.getRight(), valid.getEndDate());
			}
		} else {
			valid.setGreyValue(0);
		}
	}

	private boolean isGreyValue(SensorWarningHistory valid) {
		return valid.getRedValue() <= 0 && valid.getYellowValue() <= 0 && valid.getGreenValue() <= 0;
	}

	private void splitHistoryValue(SensorWarningHistory valid, List<DatePeriod> splitPeriods, Date validEndDate) {
		Date splitDate = null;
		Date nextSplitDate = null;
		boolean isBreakInterval = true;
		for (DatePeriod splitPeriod : splitPeriods) {
			splitDate = splitPeriod.getStartDate();
			nextSplitDate = splitPeriod.getEndDate();
			isBreakInterval = splitPeriod.isBreakInterval();
			if (splitDate != null && valid.getStartDate().getTime() < splitDate.getTime()) {
				if (nextSplitDate != null && (validEndDate == null || nextSplitDate.getTime() < validEndDate.getTime())) {
					doSplit(valid, splitDate, nextSplitDate, isBreakInterval);
				} else {
					if (validEndDate == null || splitDate.getTime() < validEndDate.getTime()) {
						doSplit(valid, splitDate, validEndDate, isBreakInterval);
					}
					break;
				}
			}
		}
		if (nextSplitDate != null && (validEndDate == null || nextSplitDate.getTime() < validEndDate.getTime())) {
			doSplit(valid, nextSplitDate, validEndDate, !isBreakInterval);
		}
	}

	private void doSplit(SensorWarningHistory valid, Date splitStartDate, Date splitEndDate, boolean isBreakInterval) {
		if (splitStartDate != null) {
			if (splitEndDate == null || splitEndDate.getTime() > splitStartDate.getTime()) {
				if (valid.getEndDate() == null || valid.getEndDate().getTime() > splitStartDate.getTime()) {
					valid.setEndDate(splitStartDate);
				}
				SensorWarningHistory split = new SensorWarningHistory();
				split.setMachine(valid.getMachine());
				split.setStartDate(splitStartDate);
				split.setEndDate(splitEndDate);
				split.setGreenValue(valid.getGreenValue());
				split.setRedValue(valid.getRedValue());
				split.setYellowValue(valid.getYellowValue());
				split.setWarningType(valid.getWarningType());
				split.setGreyValue(isBreakInterval ? 0 : 1);
				entityBeanUtil.persist(split);
			}
		}
	}

}
