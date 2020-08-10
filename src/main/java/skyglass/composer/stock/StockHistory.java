package skyglass.composer.stock;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skyglass.composer.domain.AObject;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockHistory extends AObject {

	private static final long serialVersionUID = 2538795431473160363L;

	private String uuid;

	private Item item;

	private BusinessUnit businessUnit;

	private Double amount;

	private Date startDate;

	private Date endDate;

}
