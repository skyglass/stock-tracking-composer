package skyglass.composer.stock.persistence;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.entity.AEntity;
import skyglass.composer.stock.BusinessUnit;
import skyglass.composer.stock.Item;
import skyglass.composer.stock.StockParameter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockMovementEntity extends AEntity {

	private static final long serialVersionUID = 647564188166713065L;

	@Id
	@GeneratedValue
	private String uuid;

	@ManyToOne(optional = false)
	private Item item;

	@ManyToOne(optional = false)
	private BusinessUnit from;

	@ManyToOne(optional = false)
	private BusinessUnit to;

	@Column(nullable = false)
	private Double amount;

	@Column(nullable = false)
	private Date createdAt;

	@OneToMany
	private List<StockParameter> parameters = new ArrayList<>();

}
