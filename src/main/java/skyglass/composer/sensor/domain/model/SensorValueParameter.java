
package skyglass.composer.sensor.domain.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skyglass.composer.stock.entity.model.AEntity;

@Entity
@Table(name = "sensorvalueparameter")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SensorValueParameter extends AEntity {

	private static final long serialVersionUID = 2796432866137480488L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column
	private String name;

	@Column
	private String reference;

	@Column
	private String value;

	@Column
	private Date createdAt;

	@ManyToOne
	private SensorValue sensorValue;

	@ManyToOne
	private SensorValueHistory sensorValueHistory;

}
