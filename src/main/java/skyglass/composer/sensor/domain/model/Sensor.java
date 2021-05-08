package skyglass.composer.sensor.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.AEntity;

@Entity
@Table(name = "sensor")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Sensor extends AEntity {

	private static final long serialVersionUID = 5722786330420713213L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column
	private String sensorId;

	@ManyToOne(fetch = FetchType.LAZY)
	private Device machine;

	@Enumerated(EnumType.STRING)
	private SensorValueType valueType;

	private double thresholdValue;

	public void setMachine(Device machine) {
		this.machine = machine;
	}

}
