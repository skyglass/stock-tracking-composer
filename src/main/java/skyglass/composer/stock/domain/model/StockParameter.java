package skyglass.composer.stock.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockParameter extends AObject {

	private static final long serialVersionUID = -5317017516582220570L;

	private String uuid;

	private String name;

	private String value;

}
