package skyglass.composer.sensor.domain.model;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skyglass.composer.stock.entity.model.AEntity;

@Entity
@Table(name = "sensorvalue")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SensorValue extends AEntity {

	private static final long serialVersionUID = 4308166514071671807L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column
	private double value;

	@Column
	private Date createdAt;

	@ManyToOne(fetch = FetchType.LAZY)
	private Sensor sensor;

	@ManyToOne(fetch = FetchType.LAZY)
	private Device machine;

	@OneToMany(mappedBy = "sensorValue")
	private List<SensorValueParameter> parameters;

}
