package skyglass.composer.stock.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

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
	@GeneratedValue
	private String uuid;

	@ManyToOne(optional = false)
	private ItemEntity item;

	@ManyToOne(optional = false)
	private BusinessUnitEntity businessUnit;

	@Column(nullable = false)
	private Double amount;

}
