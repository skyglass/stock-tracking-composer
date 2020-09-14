package skyglass.composer.stock.entity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stockparameter")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockParameterEntity extends AEntity {

	private static final long serialVersionUID = -3472665517413113002L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String value;

}
