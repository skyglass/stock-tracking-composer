package skyglass.composer.stock.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockParameterEntity extends AEntity {

	private static final long serialVersionUID = -3472665517413113002L;

	@Id
	@GeneratedValue
	private String uuid;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String value;

}
