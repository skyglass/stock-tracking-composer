package skyglass.composer.stock.entity.model;

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

@Entity
@Table(name = "stock")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockEntity extends AEntity {

	private static final long serialVersionUID = -3220078668293046682L;

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

	@Column
	private boolean active;

	public void updateAmount(double amount) {
		this.amount = this.amount + amount;
	}

	public static StockEntity create(ItemEntity item, BusinessUnitEntity businessUnit) {
		return new StockEntity(null, item, businessUnit, 0D, true);
	}

}
