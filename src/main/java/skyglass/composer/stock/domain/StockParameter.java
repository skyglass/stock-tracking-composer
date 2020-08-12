package skyglass.composer.stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockParameter extends AObject {

	private static final long serialVersionUID = -5317017516582220570L;

	private String uuid;

	private String name;

	private String value;

}
