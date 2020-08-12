package skyglass.composer.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Stock extends AObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1639576602087075222L;

	private String uuid;

	private Item item;

	private BusinessUnit businessUnit;

	private Double amount;

}
