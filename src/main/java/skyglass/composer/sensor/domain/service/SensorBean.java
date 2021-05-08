package skyglass.composer.sensor.domain.service;

import java.util.Collection;
import java.util.List;

import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.common.repository.AEntityRepository;
import skyglass.composer.security.entity.repository.PermissionBean;
import skyglass.composer.sensor.domain.model.Device;
import skyglass.composer.sensor.domain.model.Sensor;
import skyglass.composer.sensor.domain.model.SensorValueType;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.exceptions.NotAccessibleException;

@Repository
@Transactional
public class SensorBean extends AEntityRepository<Sensor> {

	@Autowired
	private PermissionBean permissionBean;

	@Override
	public Sensor findByUuid(String uuid) {
		if (uuid == null) {
			return null;
		}

		TypedQuery<Sensor> query = sensorQuery(null, "s.uuid = :uuid");
		query.setParameter("uuid", uuid);
		return EntityUtil.getSingleResultSafely(query);
	}

	@NotNull
	@Override
	public Collection<Sensor> findAll() {
		TypedQuery<Sensor> query = sensorQuery(null, null);
		return EntityUtil.getListResultSafely(query);
	}

	private TypedQuery<Sensor> sensorQuery(String joins, String whereExtension) {
		String queryStr = getDefaultQuery(joins);
		if (whereExtension != null) {
			if (queryStr.contains("WHERE")) {
				queryStr += " AND " + whereExtension;
			} else {
				queryStr += " WHERE " + whereExtension;
			}
		}

		return buildQuery(queryStr);
	}

	private TypedQuery<Sensor> buildQuery(String queryStr) {
		TypedQuery<Sensor> query = entityBeanUtil.createQuery(queryStr, Sensor.class);

		return query;
	}

	private String getDefaultQuery(String joins) {
		String query = "SELECT DISTINCT(s) FROM Sensor s";

		if (!StringUtils.isBlank(joins)) {
			query += joins;
		}

		return query;
	}

	public List<Sensor> findSensorsByMachine(String machineUUID) {
		TypedQuery<Sensor> query = sensorQuery("JOIN s.machine m", "m.uuid = :machineUUID ");
		query.setParameter("machineUUID", machineUUID);
		List<Sensor> sensorList = EntityUtil.getListResultSafely(query);
		if (sensorList.isEmpty()) {
			throw new NotAccessibleException(Device.class, Sensor.class, machineUUID);
		}
		return sensorList;
	}

	List<Sensor> findSensorsByMachineAndType(String machineUuid, SensorValueType sensorValueType) {
		String queryStr = "SELECT DISTINCT s FROM Sensor s JOIN SensorValue v ON v.sensor = s WHERE s.machine.uuid = :machineUuid AND s.valueType = :valueType AND s.sensorType = :sensorType AND v.value != -1";
		TypedQuery<Sensor> query = entityBeanUtil.createQuery(queryStr, Sensor.class);
		query.setParameter("machineUuid", machineUuid);
		query.setParameter("valueType", sensorValueType);
		return EntityUtil.getListResultSafely(query);
	}

}
