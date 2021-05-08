package skyglass.composer.sensor.domain.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
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
@Table(name = "sensorvaluehistory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SensorValueHistory extends AEntity {

	private static final long serialVersionUID = 2555855389862652463L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column
	private Date startDate;

	@Column
	private Date endDate;

	@Column
	private Double value;

	@ManyToOne(fetch = FetchType.LAZY)
	private Sensor sensor;

	@ManyToOne(fetch = FetchType.LAZY)
	private Device machine;

	@OneToMany(mappedBy = "sensorValueHistory", cascade = { CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH })
	private List<SensorValueParameter> parameters;

	@Column
	private String sensorValueUuid;

}
