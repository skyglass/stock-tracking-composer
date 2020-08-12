package skyglass.composer.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Item extends AObject {

	private static final long serialVersionUID = -4855746732917542351L;

	private String uuid;

	private String name;

}
