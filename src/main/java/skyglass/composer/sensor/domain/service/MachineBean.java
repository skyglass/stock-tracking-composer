package skyglass.composer.sensor.domain.service;

import java.util.Collection;
import java.util.Collections;
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
import skyglass.composer.sensor.domain.model.SensorValueType;
import skyglass.composer.sensor.domain.model.WarningLightStatus;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.exceptions.NotAccessibleException;

@Repository
@Transactional
public class MachineBean extends AEntityRepository<Device> {

	@Override
	public Device findByUuid(String uuid) {
		if (StringUtils.isBlank(uuid)) {
			return null;
		}
		TypedQuery<Device> query = machineQuery(null, "m.uuid = :uuid");
		query.setParameter("uuid", uuid);

		Device machine = EntityUtil.getSingleResultSafely(query);
		return machine;
	}

	@NotNull
	@Override
	public Collection<Device> findAll() {
		TypedQuery<Device> query = machineQuery(null, null);
		List<Device> list = EntityUtil.getListResultSafely(query);
		return list;
	}

	@NotNull
	public List<Device> findByUuids(Collection<String> uuids) {
		if (CollectionUtils.isEmpty(uuids)) {
			return Collections.emptyList();
		}
		String whereExtension = "m.uuid in ('" + String.join("','", uuids) + "')";
		TypedQuery<Device> query = machineQuery(null, whereExtension);

		List<Device> list = EntityUtil.getListResultSafely(query);
		return list;
	}

	public Device findMachineBySensorUuid(String sensorUuid) {
		TypedQuery<Device> buildQuery = machineQuery("JOIN m.sensors s", "s.uuid = :sensorUuid");
		buildQuery.setParameter("sensorUuid", sensorUuid);
		Device machine = EntityUtil.getSingleResultSafely(buildQuery);
		if (machine == null) {
			throw new NotAccessibleException(Device.class, Sensor.class, sensorUuid);
		}
		return machine;
	}

	private TypedQuery<Device> machineQuery(String queryStr, String whereExtension) {
		if (whereExtension != null) {
			queryStr += " " + whereExtension;
		}

		TypedQuery<Device> query = entityBeanUtil.createQuery(queryStr, Device.class);
		return query;
	}

	private static WarningLightStatus getStatusFromValue(double value) {
		double precision = 0.001d;
		if (Math.abs(value) < precision) {
			return WarningLightStatus.Off;
		} else if (Math.abs(value - 1d) < precision) {
			return WarningLightStatus.On;
		} else if (Math.abs(value - 2d) < precision) {
			return WarningLightStatus.Toggling;
		} else {
			return WarningLightStatus.Unknown;
		}
	}

	public static int getWarningLightStatusValue(Sensor sensor, double value) {
		if (sensor.getValueType() == SensorValueType.State) {
			return getStatusFromValue(value).getValue();
		}
		if (sensor.getThresholdValue() == 0) {
			return WarningLightStatus.Unknown.getValue();
		}
		return value >= sensor.getThresholdValue() ? WarningLightStatus.On.getValue() : WarningLightStatus.Off.getValue();
	}

}
