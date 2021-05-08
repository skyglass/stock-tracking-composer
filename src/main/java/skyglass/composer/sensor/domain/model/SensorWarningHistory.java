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
@Table(name = "sensorwarninghistory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class SensorWarningHistory extends AEntity {

	private static final long serialVersionUID = -1151186484109480474L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column
	private Date startDate;

	@Column
	private Date endDate;

	@Column
	private int redValue;

	@Column
	private int yellowValue;

	@Column
	private int greenValue;

	@Column
	private int greyValue;

	@ManyToOne
	private Device machine;

	@Column
	private WarningType warningType;

}
