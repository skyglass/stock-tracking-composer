package skyglass.composer.stock;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skyglass.composer.domain.AObject;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class BusinessUnit extends AObject {

	private static final long serialVersionUID = -4855746732917542351L;

	private String uuid;

	private String name;

}
