package skyglass.composer.stock.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.entity.AEntity;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ItemEntity extends AEntity {

	private static final long serialVersionUID = 4928046276251456307L;

	@Id
	@GeneratedValue
	private String uuid;

	@Column(nullable = false)
	private String name;

}
