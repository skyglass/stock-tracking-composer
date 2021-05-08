package skyglass.composer.sensor.domain.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
public class ScheduleItem extends AEntity {

	private static final long serialVersionUID = -1538227299859082062L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Enumerated(EnumType.STRING)
	private Day day;

	private String timezone;

	//representation in hh:mm
	private String startTime;

	//representation in hh:mm
	private String endTime;

	private Date validFrom;

	private Date validTo;

	private String description;

	@ManyToOne
	private Device machine;

}
