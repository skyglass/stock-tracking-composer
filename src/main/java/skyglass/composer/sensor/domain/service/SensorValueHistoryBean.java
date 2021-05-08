package skyglass.composer.sensor.domain.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.sensor.domain.model.Device;
import skyglass.composer.sensor.domain.model.Sensor;
import skyglass.composer.sensor.domain.model.SensorValue;
import skyglass.composer.sensor.domain.model.SensorValueDTO;
import skyglass.composer.sensor.domain.model.SensorValueHistory;
import skyglass.composer.sensor.domain.model.SensorValueParameterDTOFactory;
import skyglass.composer.stock.domain.model.CrudAction;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.stock.exceptions.NotAllowedException;
import skyglass.composer.stock.exceptions.NotUniqueException;

@Repository
@Transactional
public class SensorValueHistoryBean extends AEntityRepository<SensorValueHistory> {

	@Override
	public SensorValueHistory findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		TypedQuery<SensorValueHistory> query = sensorValueHistoryQuery(null, "svh.uuid = :uuid");
		query.setParameter("uuid", uuid);
		SensorValueHistory sensorValueHistory = EntityUtil.getSingleResultSafely(query);
		if (sensorValueHistory == null) {
			throw new NotAccessibleException(SensorValueHistory.class, uuid);
		}
		return sensorValueHistory;
	}

	public SensorValueHistory findBySensorValueUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		TypedQuery<SensorValueHistory> query = sensorValueHistoryQuery(null, "svh.sensorValueUuid = :uuid");
		query.setParameter("uuid", uuid);

		List<SensorValueHistory> sensorValueHistory = EntityUtil.getListResultSafely(query);
		if (sensorValueHistory.isEmpty()) {
			return null;
		} else if (sensorValueHistory.size() > 1) {
			throw new NotUniqueException("There are more than one SensorValueHistory with the same SensorValueUuid!");
		}

		return sensorValueHistory.get(0);
	}

	@Override
	public Collection<SensorValueHistory> findAll() {
		throw new NotAllowedException(SensorValueHistory.class, CrudAction.READ);
	}

	private TypedQuery<SensorValueHistory> sensorValueHistoryQuery(String joins, String whereExtension) {
		String queryStr = getDefaultQuery(joins);
		if (whereExtension != null) {
			queryStr += " AND " + whereExtension;
		}
		return buildQuery(queryStr);
	}

	private TypedQuery<SensorValueHistory> buildQuery(String queryStr) {
		TypedQuery<SensorValueHistory> query = entityBeanUtil.createQuery(queryStr, SensorValueHistory.class);
		return query;
	}

	private String getDefaultQuery(String joins) {
		String query = "SELECT DISTINCT(svh) FROM SensorValueHistory svh ";

		if (!StringUtils.isBlank(joins)) {
			query += joins;
		}
		return query;
	}

	@NotNull
	public Collection<SensorValueHistory> getSensorValueHistoryOfSensor(String sensorUuid, Date fromDate, Date toDate) {
		String whereExtension = "svh.sensor.uuid = :sensorUuid";

		if (toDate != null) {
			whereExtension += " AND svh.startDate <= :toDate";
		}
		if (fromDate != null) {
			whereExtension += " AND svh.endDate >= :fromDate";
		}

		whereExtension += " ORDER BY svh.startDate DESC";

		TypedQuery<SensorValueHistory> query = sensorValueHistoryQuery(null, whereExtension);

		if (fromDate != null) {
			query.setParameter("fromDate", fromDate);
		}
		if (toDate != null) {
			query.setParameter("toDate", toDate);
		}

		query.setParameter("sensorUuid", sensorUuid);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	public List<SensorValueHistory> findForSensor(Sensor sensor) {
		TypedQuery<SensorValueHistory> query = findForSensorQuery(sensor);
		return EntityUtil.getListResultSafely(query);
	}

	public SensorValueHistory findLatest(Sensor sensor) {
		TypedQuery<SensorValueHistory> query = findForSensorQuery(sensor);
		query.setMaxResults(1);
		List<SensorValueHistory> result = EntityUtil.getListResultSafely(query);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	private TypedQuery<SensorValueHistory> findForSensorQuery(Sensor sensor) {
		String queryStr = "SELECT svh FROM SensorValueHistory svh WHERE svh.sensor.uuid = :sensorUuid ORDER BY svh.startDate DESC";
		TypedQuery<SensorValueHistory> query = entityBeanUtil.createQuery(queryStr, SensorValueHistory.class);
		query.setParameter("sensorUuid", sensor.getUuid());
		return query;
	}

	public SensorValueHistory findValidForSensor(Sensor sensor, Date validityDate) {
		List<SensorValueHistory> result = findValidPreviousListForSensor(sensor, validityDate);
		return CollectionUtils.isEmpty(result) ? null : result.get(0);
	}

	@NotNull
	private List<SensorValueHistory> findValidPreviousListForSensor(Sensor sensor, Date validityDate) {
		String queryStr = "SELECT svh FROM SensorValueHistory svh WHERE svh.sensor.uuid = :sensorUuid AND svh.startDate <= :validityDate AND (svh.endDate IS NULL OR svh.endDate > :validityDate) ORDER BY svh.startDate";
		TypedQuery<SensorValueHistory> query = entityBeanUtil.createQuery(queryStr, SensorValueHistory.class);
		query.setParameter("sensorUuid", sensor.getUuid());
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	private List<SensorValueHistory> findValidNextListForSensor(Sensor sensor, Date validityDate) {
		String queryStr = "SELECT svh FROM SensorValueHistory svh WHERE svh.sensor.uuid = :sensorUuid AND svh.startDate > :validityDate ORDER BY svh.startDate";
		TypedQuery<SensorValueHistory> query = entityBeanUtil.createQuery(queryStr, SensorValueHistory.class);
		query.setParameter("sensorUuid", sensor.getUuid());
		query.setParameter("validityDate", validityDate);
		return EntityUtil.getListResultSafely(query);
	}

	@NotNull
	public List<SensorValueHistory> findValidListForSensorAndPeriod(String sensorUuid, Date startDate, Date endDate) {
		String queryStr = "SELECT svh FROM SensorValueHistory svh WHERE svh.sensor.uuid = :sensorUuid "
				+ (endDate == null ? "" : "AND svh.startDate < :endDate ")
				+ (startDate == null ? "" : "AND (svh.endDate IS NULL OR svh.endDate > :startDate) ")
				+ "ORDER BY svh.startDate DESC";
		TypedQuery<SensorValueHistory> query = entityBeanUtil.createQuery(queryStr, SensorValueHistory.class);
		query.setParameter("sensorUuid", sensorUuid);
		if (startDate != null) {
			query.setParameter("startDate", startDate);
		}
		if (endDate != null) {
			query.setParameter("endDate", endDate);
		}
		return EntityUtil.getListResultSafely(query);
	}

	public SensorValueHistory createHistoryForSensorValueDTO(SensorValueDTO dto) {
		Sensor sensor = entityBeanUtil.find(Sensor.class, dto.getSensorUuid());
		return createHistoryForSensorValue(sensor, dto);
	}

	@NotNull
	private SensorValueHistory createHistoryForSensorValue(Sensor sensor, SensorValueDTO sensorValueDto) {
		Date validityDate = sensorValueDto.getCreatedAt();
		double doubleValue = sensorValueDto.getValue();

		Device machine = sensor.getMachine();

		List<SensorValueHistory> previousList = findValidPreviousListForSensor(sensor, validityDate);
		SensorValueHistory previous = null;
		if (CollectionUtils.isNotEmpty(previousList)) {
			if (previousList.size() > 1) {
				for (int i = 1; i < previousList.size(); i++) {
					entityBeanUtil.remove(previousList.get(i));
				}
			}
			previous = previousList.get(0);
		}

		SensorValueHistory valid = new SensorValueHistory();
		valid.setMachine(machine);
		valid.setStartDate(validityDate);
		valid.setValue(doubleValue);
		valid.setSensor(sensor);
		valid.setSensorValueUuid(sensorValueDto.getUuid());
		if (CollectionUtils.isNotEmpty(sensorValueDto.getParameters())) {
			valid.setParameters(SensorValueParameterDTOFactory.createSensorValueParameters(sensorValueDto.getParameters()));
			valid.getParameters().stream().forEach(e -> {
				e.setSensorValue(null);
				e.setSensorValueHistory(valid);
				e.setCreatedAt(validityDate);
			});
		}

		if (previous != null) {
			valid.setEndDate(previous.getEndDate());
			previous.setEndDate(validityDate);
			entityBeanUtil.merge(previous);
		}

		List<SensorValueHistory> nextList = findValidNextListForSensor(sensor, validityDate);

		if (CollectionUtils.isNotEmpty(nextList)) {
			valid.setEndDate(nextList.get(0).getStartDate());
		}

		if (previous != null && previous.getStartDate().equals(previous.getEndDate())) {
			entityBeanUtil.remove(previous);
		}

		Collection<SensorValue> sensorValues = findLatestSensorValues(sensor);
		int i = 0;
		for (SensorValue value : sensorValues) {
			if (i > 0) {
				entityBeanUtil.remove(value);
			}
			i++;
		}

		return super.createEntity(valid);
	}

	private Collection<SensorValue> findLatestSensorValues(Sensor sensor) {
		String queryStr = "SELECT sv FROM SensorValue sv WHERE sv.sensor.uuid = :sensorUuid ORDER BY sv.createdAt DESC";
		TypedQuery<SensorValue> query = entityBeanUtil.createQuery(queryStr, SensorValue.class);
		query.setParameter("sensorUuid", sensor.getUuid());
		return EntityUtil.getListResultSafely(query);
	}

}
