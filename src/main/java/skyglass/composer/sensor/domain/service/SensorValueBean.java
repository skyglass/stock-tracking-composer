package skyglass.composer.sensor.domain.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.TypedQuery;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.sensor.domain.model.Sensor;
import skyglass.composer.sensor.domain.model.SensorValue;
import skyglass.composer.sensor.domain.model.SensorValueDTO;
import skyglass.composer.sensor.domain.model.SensorValueParameter;
import skyglass.composer.sensor.domain.model.SensorValueParameterDTO;
import skyglass.composer.sensor.domain.model.SensorValueParameterDTOFactory;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.utils.date.DateUtil;

@Repository
@Transactional
public class SensorValueBean extends AEntityRepository<SensorValue> {

	public static final String SENSORID_RED = "RED";

	public static final String SENSORID_YELLOW = "YELLOW";

	public static final String SENSORID_GREEN = "GREEN";

	@Autowired
	private SensorBean sensorBean;

	@Override
	public SensorValue findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}
		SensorValue value = super.findByUuid(uuid);

		return value;
	}

	public SensorValueDTO createSensorValue(SensorValueDTO dto) {
		Sensor sensor = sensorBean.findByUuidSecure(dto.getSensorUuid());
		return createSensorValue(dto, sensor);
	}

	public SensorValueDTO createSensorValue(SensorValueDTO dto, Sensor sensor) {
		Date createdAt = dto.getCreatedAt() == null ? DateUtil.now() : dto.getCreatedAt();
		double value = dto.getValue();

		SensorValue sensorValue = createSensorValue(value, sensor, createdAt);

		entityBeanUtil.persist(sensorValue);
		updateParameters(dto, sensorValue, createdAt);
		return decorateDto(dto, sensor, sensorValue, createdAt);
	}

	private SensorValueDTO decorateDto(SensorValueDTO dto, Sensor sensor, SensorValue sensorValue, Date createdAt) {
		dto.setUuid(sensorValue == null ? null : sensorValue.getUuid());
		dto.setCreatedAt(createdAt);
		dto.setMachineUuid(sensor.getMachine().getUuid());
		decorateDtoParameters(dto, sensorValue, createdAt);
		return dto;
	}

	private void decorateDtoParameters(SensorValueDTO dto, SensorValue sensorValue, Date createdAt) {
		if (CollectionUtils.isEmpty(sensorValue.getParameters())) {
			return;
		}
		List<SensorValueParameterDTO> parameters = SensorValueParameterDTOFactory.createSensorValueParameterDTOs(sensorValue.getParameters());
		for (SensorValueParameterDTO svp : parameters) {
			svp.setCreatedAt(createdAt);
		}
		dto.setParameters(parameters);
	}

	private SensorValue createSensorValue(double value, Sensor sensor, Date createdAt) {
		SensorValue sensorValue = new SensorValue();
		sensorValue.setSensor(sensor);
		sensorValue.setMachine(sensor.getMachine());
		sensorValue.setValue(value);
		sensorValue.setCreatedAt(createdAt);
		return sensorValue;
	}

	private void updateParameters(SensorValueDTO dto, SensorValue sensorValue, Date createdAt) {
		if (CollectionUtils.isEmpty(dto.getParameters())) {
			return;
		}
		if (sensorValue != null && CollectionUtils.isNotEmpty(sensorValue.getParameters())) {
			for (SensorValueParameter svp : sensorValue.getParameters()) {
				entityBeanUtil.remove(svp);
			}
		}
		List<SensorValueParameter> parameters = SensorValueParameterDTOFactory.createSensorValueParameters(dto.getParameters());
		for (SensorValueParameter svp : parameters) {
			svp.setUuid(null);
			svp.setCreatedAt(createdAt);
			svp.setSensorValue(sensorValue);
			svp.setSensorValueHistory(null);
			entityBeanUtil.persist(svp);
		}
		sensorValue.setParameters(parameters);
	}

	public Collection<SensorValue> getSensorValuesOfSensor(String sensorUuid, Date fromDate, Date toDate) {
		String queryStr = "SELECT DISTINCT sv FROM SensorValue sv WHERE sv.createdAt >= :fromDate AND sv.createdAt <= :toDate AND sv.sensor.uuid = :sensorUuid "
				+ " ORDER BY sv.createdAt DESC ";

		TypedQuery<SensorValue> buildQuery = entityBeanUtil.createQuery(queryStr, SensorValue.class);
		buildQuery.setParameter("sensorUuid", sensorUuid);
		buildQuery.setParameter("fromDate", fromDate);
		buildQuery.setParameter("toDate", toDate);

		return EntityUtil.getListResultSafely(buildQuery);
	}

	public SensorValue getLatestValueOfSensor(String sensorUuid) {
		String queryStr = "SELECT DISTINCT sv FROM SensorValue sv "
				+ "WHERE sv.sensor.uuid = :sensorUuid ORDER BY sv.createdAt DESC ";

		TypedQuery<SensorValue> buildQuery = entityBeanUtil.createQuery(queryStr, SensorValue.class);
		buildQuery.setParameter("sensorUuid", sensorUuid);

		return EntityUtil.getSingleResultSafely(buildQuery);
	}

}
