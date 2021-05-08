package skyglass.composer.sensor.domain.service;

import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.EntityBeanUtil;
import skyglass.composer.sensor.domain.model.Sensor;
import skyglass.composer.sensor.domain.model.SensorValue;
import skyglass.composer.sensor.domain.model.SensorValueDTO;
import skyglass.composer.sensor.domain.model.SensorValueDTOFactory;
import skyglass.composer.sensor.domain.model.SensorValueType;
import skyglass.composer.stock.exceptions.ClientException;
import skyglass.composer.stock.exceptions.NotAccessibleException;
import skyglass.composer.stock.exceptions.NotNullableException;
import skyglass.composer.utils.date.DateUtil;

@Service
public class SensorValueService {

	private static final Logger log = LoggerFactory.getLogger(SensorValueService.class);

	private static final String SENSOR_VALUE_HISTORY_OWNER = "SVH";

	private static final String SENSOR_WARNING_HISTORY_OWNER = "SWH";

	@Autowired
	private SensorValueBean sensorValueBean;

	@Autowired
	private SensorBean sensorBean;

	@Autowired
	private EntityBeanUtil entityBeanUtil;

	@Autowired
	private SensorValueHistoryBean sensorValueHistoryBean;

	@Autowired
	private SensorWarningHistoryBean sensorWarningHistoryBean;

	@Autowired
	private DatabaseLockManager lockManager;

	public SensorValueDTO findByUuid(String uuid) {
		// fetch from database
		SensorValue sensorValue = sensorValueBean.findByUuid(uuid);
		if (sensorValue == null) {
			throw new NotAccessibleException(SensorValue.class, uuid);
		}
		// convert to DTO
		SensorValueDTO dto = SensorValueDTOFactory.createSensorValueDTO(sensorValue);
		return dto;
	}

	@NotNull
	public SensorValueDTO createSensorValue(SensorValueDTO dto) throws NotAccessibleException {
		if (dto == null) {
			throw new NotNullableException(SensorValueDTO.class);
		}
		if (dto.getSensorUuid() == null) {
			throw new NotNullableException("sensorUuid");
		}
		dto = sensorValueBean.createSensorValue(dto);
		saveHistory(dto);
		return dto;
	}

	@Transactional
	public SensorValueDTO createSensorValueForScheduler(SensorValueDTO dto) throws NotAccessibleException {
		Sensor sensor = entityBeanUtil.find(Sensor.class, dto.getSensorUuid());
		return createSensorValueForScheduler(dto, sensor);
	}

	private SensorValueDTO createSensorValueForScheduler(SensorValueDTO dto, Sensor sensor) throws NotAccessibleException {
		dto = sensorValueBean.createSensorValue(dto, sensor);
		saveHistory(dto);
		return dto;
	}

	@NotNull
	public Collection<SensorValueDTO> findInPeriodBySensor(String sensorUuid, String from, String to) {
		// date parsing
		Date dateFrom;
		Date dateTo;
		try {
			dateFrom = DateUtil.parseDateTime(from);
			dateTo = DateUtil.parseDateTime(to);
		} catch (DateTimeParseException ex) {
			// could be just a day given
			dateFrom = DateUtil.parse(from);
			dateTo = DateUtil.parse(to);
		}

		if (dateFrom.after(dateTo)) {
			log.error("Period FromDate cannot be after period ToDate");
			throw new ClientException(HttpStatus.BAD_REQUEST, "Period FromDate cannot be after period ToDate");
		}

		Collection<SensorValue> sensorValues = sensorValueBean.getSensorValuesOfSensor(sensorUuid, dateFrom, dateTo);
		return SensorValueDTOFactory.createSensorValueDTOs(sensorValues);
	}

	@Transactional
	public List<SensorValueDTO> setWarningLightSensorsToUnknown(String machineUuid) {
		List<SensorValueDTO> result = new ArrayList<>();
		List<Sensor> warningLightSensors = sensorBean.findSensorsByMachineAndType(machineUuid, SensorValueType.State);
		for (Sensor s : warningLightSensors) {
			SensorValueDTO dto = new SensorValueDTO();
			dto.setValue(-1);
			dto.setSensorUuid(s.getUuid());
			result.add(createSensorValueForScheduler(dto, s));
		}
		return result;
	}

	private void saveHistory(SensorValueDTO dto) {
		lockManager.tryLock(dto.getMachineUuid(), SENSOR_VALUE_HISTORY_OWNER);
		try {
			sensorValueHistoryBean.createHistoryForSensorValueDTO(dto);
		} finally {
			lockManager.releaseLock(dto.getMachineUuid(), SENSOR_VALUE_HISTORY_OWNER);
		}
		lockManager.tryLock(dto.getMachineUuid(), SENSOR_WARNING_HISTORY_OWNER);
		try {
			sensorWarningHistoryBean.createHistoryForSensorValueDTO(dto);
		} finally {
			lockManager.releaseLock(dto.getMachineUuid(), SENSOR_WARNING_HISTORY_OWNER);
		}
	}
}
