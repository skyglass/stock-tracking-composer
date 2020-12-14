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
import skyglass.composer.security.entity.model.ContextEntity;

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
	private ContextEntity context;

	@Column(nullable = false)
	private Double amount;

	@Column
	private boolean active;

	public void updateAmount(double amount) {
		this.amount = this.amount + amount;
	}

	public void deactivate() {
		this.active = false;
	}

	public static StockEntity create(ItemEntity item, ContextEntity context) {
		return new StockEntity(null, item, context, 0D, true);
	}

}
