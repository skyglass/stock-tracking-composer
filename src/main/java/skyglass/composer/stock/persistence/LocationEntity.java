package skyglass.composer.stock.persistence;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.entity.AEntity;

@Entity
@Table(name = "location")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class LocationEntity extends AEntity {

	private static final long serialVersionUID = 8750966701381584635L;

	@NotNull
	private String name;

}
