package skyglass.composer.stock.persistence.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stockmessage")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockMessageEntity extends AEntity {

	private static final long serialVersionUID = 647564188166713065L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@ManyToOne(optional = false)
	private BusinessUnitEntity from;

	@ManyToOne(optional = false)
	private BusinessUnitEntity to;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private Long offset;

	@Column(nullable = false)
	private Date createdAt;

	@Column(nullable = false)
	private String messageId;

	@OneToMany
	private List<StockParameterEntity> parameters = new ArrayList<>();

}
