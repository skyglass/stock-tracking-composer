package skyglass.composer.security.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.domain.model.AObject;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Owner extends AObject {

	private static final long serialVersionUID = -4855746732917542351L;

	private String uuid;

	private String name;

}
