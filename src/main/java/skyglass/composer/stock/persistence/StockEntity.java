package skyglass.composer.stock.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.entity.AEntity;
import skyglass.composer.stock.BusinessUnit;
import skyglass.composer.stock.Item;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockEntity extends AEntity {

	private static final long serialVersionUID = -3220078668293046682L;

	@Id
	@GeneratedValue
	private String uuid;

	@ManyToOne(optional = false)
	private Item item;

	@ManyToOne(optional = false)
	private BusinessUnit businessUnit;

	@Column(nullable = false)
	private Double amount;

}
