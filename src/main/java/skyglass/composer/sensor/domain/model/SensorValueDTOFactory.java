package skyglass.composer.sensor.domain.model;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import skyglass.composer.stock.domain.dto.AEntityDTOFactory;

public class SensorValueDTOFactory extends AEntityDTOFactory {

	public static SensorValueDTO createSensorValueDTO(SensorValue sensorValue) {
		if (sensorValue == null) {
			return null;
		}

		SensorValueDTO sensorValueDTO = new SensorValueDTO();
		sensorValueDTO.setUuid(sensorValue.getUuid());
		sensorValueDTO.setValue(sensorValue.getValue());
		sensorValueDTO.setCreatedAt(sensorValue.getCreatedAt());
		sensorValueDTO.setParameters(SensorValueParameterDTOFactory.createSensorValueParameterDTOs(sensorValue.getParameters()));

		Sensor sensor = sensorValue.getSensor();
		if (sensor != null) {
			sensorValueDTO.setSensorUuid(sensor.getUuid());
		}
		if (sensor.getMachine() != null) {
			sensorValueDTO.setMachineUuid(sensorValue.getMachine().getUuid());
		}

		return sensorValueDTO;
	}

	public static SensorValue createSensorValue(SensorValueDTO dto) {
		if (dto == null) {
			return null;
		}

		SensorValue sensorValue = new SensorValue();
		if (dto.getUuid() != null && !dto.getUuid().isEmpty()) {
			sensorValue.setUuid(dto.getUuid());
		}
		sensorValue.setValue(dto.getValue());
		sensorValue.setCreatedAt(dto.getCreatedAt());
		sensorValue.setMachine(createVeryBasicEntity(dto.getMachineUuid(), () -> new Device()));
		sensorValue.setSensor(createVeryBasicEntity(dto.getSensorUuid(), () -> new Sensor()));
		sensorValue.setParameters(SensorValueParameterDTOFactory.createSensorValueParameters(dto.getParameters()));

		return sensorValue;
	}

	public static List<SensorValueDTO> createSensorValueDTOs(Collection<SensorValue> sensorValues) {
		if (sensorValues == null) {
			return null;
		}
		return sensorValues.stream().map(value -> createSensorValueDTO(value)).collect(Collectors.toList());
	}

	public static List<SensorValue> createSensorValues(Collection<SensorValueDTO> sensorValueDTOs) {
		if (sensorValueDTOs == null) {
			return null;
		}
		return sensorValueDTOs.stream().map(dto -> createSensorValue(dto)).collect(Collectors.toList());
	}
}
