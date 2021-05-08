
package skyglass.composer.sensor.domain.model;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import skyglass.composer.stock.domain.dto.AEntityDTOFactory;

public class SensorValueParameterDTOFactory extends AEntityDTOFactory {

	public static SensorValueParameterDTO createSensorValueParameterDTO(SensorValueParameter entity) {
		if (entity == null) {
			return null;
		}
		SensorValueParameterDTO dto = new SensorValueParameterDTO();
		dto.setName(entity.getName());
		dto.setReference(entity.getReference());
		dto.setValue(entity.getValue());
		dto.setUuid(entity.getUuid());
		dto.setCreatedAt(entity.getCreatedAt());
		return dto;
	}

	public static SensorValueParameter createSensorValueParameter(SensorValueParameterDTO dto) {
		if (dto == null) {
			return null;
		}
		SensorValueParameter entity = new SensorValueParameter();
		entity.setName(dto.getName());
		entity.setReference(dto.getReference());
		entity.setValue(dto.getValue());
		return entity;
	}

	public static List<SensorValueParameterDTO> createSensorValueParameterDTOs(List<SensorValueParameter> entities) {
		if (CollectionUtils.isEmpty(entities)) {
			return Collections.emptyList();
		}
		return entities.stream().map(entity -> createSensorValueParameterDTO(entity)).filter(dto -> dto != null).collect(Collectors.toList());
	}

	public static List<SensorValueParameter> createSensorValueParameters(List<SensorValueParameterDTO> dtos) {
		if (CollectionUtils.isEmpty(dtos)) {
			return Collections.emptyList();
		}
		return dtos.stream().map(dto -> createSensorValueParameter(dto)).filter(entity -> entity != null).collect(Collectors.toList());
	}
}
