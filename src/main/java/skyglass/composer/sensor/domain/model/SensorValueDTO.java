package skyglass.composer.sensor.domain.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import skyglass.composer.stock.domain.dto.AEntityDTO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SensorValueDTO extends AEntityDTO {

	private static final long serialVersionUID = 3373936329090147467L;

	private double value;

	private Date createdAt;

	private String sensorUuid;

	private String machineUuid;

	private List<SensorValueParameterDTO> parameters;

	private String userUuid;

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getSensorUuid() {
		return sensorUuid;
	}

	public void setSensorUuid(String sensorUuid) {
		this.sensorUuid = sensorUuid;
	}

	public String getMachineUuid() {
		return machineUuid;
	}

	public void setMachineUuid(String machineUuid) {
		this.machineUuid = machineUuid;
	}

	public List<SensorValueParameterDTO> getParameters() {
		return parameters;
	}

	public void setParameters(List<SensorValueParameterDTO> parameters) {
		this.parameters = parameters;
	}

	public String getUserUuid() {
		return userUuid;
	}

	public void setUserUuid(String userUuid) {
		this.userUuid = userUuid;
	}

}
