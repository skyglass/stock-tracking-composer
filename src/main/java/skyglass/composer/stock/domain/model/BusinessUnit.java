package skyglass.composer.stock.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class BusinessUnit extends AObject {

	private static final long serialVersionUID = -4855746732917542351L;

	private String uuid;

	private String name;

	private BusinessOwner owner;

	private BusinessUnit parent;

}
