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
@Table(name = "stockhistory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockHistoryEntity extends AEntity {

	private static final long serialVersionUID = -4125237186875959873L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@ManyToOne(optional = false)
	private BusinessUnitEntity businessUnit;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private Date startDate;

	@Column(nullable = true)
	private Date endDate;

	@OneToMany
	private List<StockParameterEntity> parameters = new ArrayList<>();

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

}
