package skyglass.composer.sensor.domain.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.AEntity;

@Entity
@Table(name = "machine")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Device extends AEntity {

	private static final long serialVersionUID = 4117374615547843730L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column
	private String assetId;

	@Column
	private String machineType;

	@OneToMany(mappedBy = "machine", fetch = FetchType.LAZY)
	private List<Sensor> sensors = new ArrayList<>();

	@OneToMany(mappedBy = "machine", fetch = FetchType.LAZY)
	private List<SensorValue> timeseries = new ArrayList<>();

	public void addSensor(Sensor sensor) {
		if (sensor != null) {
			Device machine = sensor.getMachine();
			if (machine != null && !this.equals(machine)) {
				machine.removeSensor(sensor);
			}
			if (sensors == null) {
				sensors = new ArrayList<>();
			}
			if (!sensors.contains(sensor)) {
				sensors.add(sensor);
			}

			sensor.setMachine(this);
		}
	}

	public void removeSensor(Sensor sensor) {
		if (sensor != null) {
			sensors.remove(sensor);
			sensor.setMachine(null);
		}
	}

	public String getAssetId() {
		return assetId;
	}

	public void setAssetId(String assetId) {
		this.assetId = assetId;
	}

	public String getMachineType() {
		return machineType;
	}

	public void setMachineType(String machineType) {
		this.machineType = machineType;
	}

	public List<Sensor> getSensors() {
		return sensors;
	}

	public void setSensors(List<Sensor> sensors) {
		this.sensors = sensors;
	}

	public List<SensorValue> getTimeseries() {
		return timeseries;
	}

	public void setTimeseries(List<SensorValue> timeseries) {
		this.timeseries = timeseries;
	}

	@Override
	public String getUuid() {
		return uuid;
	}

}
