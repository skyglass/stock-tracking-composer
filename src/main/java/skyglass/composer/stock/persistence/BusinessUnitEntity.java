package skyglass.composer.stock.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "businessunit")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BusinessUnitEntity extends AEntity {

	private static final long serialVersionUID = 8750966701381584635L;

	@Id
	@GeneratedValue
	private String uuid;

	@Column(nullable = false)
	private String name;

}
