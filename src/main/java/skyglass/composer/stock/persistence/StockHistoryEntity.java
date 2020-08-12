package skyglass.composer.stock.persistence;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;

@Entity
@Table(name = "stockhistory")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockHistoryEntity extends AEntity {

	private static final long serialVersionUID = -4125237186875959873L;

	@Id
	@GeneratedValue
	private String uuid;

	@ManyToOne(optional = false)
	private Item item;

	@ManyToOne(optional = false)
	private BusinessUnit businessUnit;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private Date startDate;

	@Column(nullable = true)
	private Date endDate;

}
