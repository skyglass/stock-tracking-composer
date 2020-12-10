package skyglass.composer.stock.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockHistory extends AObject {

	private static final long serialVersionUID = 2538795431473160363L;

	private String uuid;

	private Item item;

	private BusinessUnit businessUnit;

	private Double amount;

	private Date startDate;

	private Date endDate;

	private List<StockParameter> parameters = new ArrayList<>();

}
