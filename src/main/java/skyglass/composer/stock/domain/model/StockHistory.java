package skyglass.composer.stock.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.security.domain.model.Context;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockHistory extends AObject {

	private static final long serialVersionUID = 2538795431473160363L;

	private String uuid;

	private Item item;

	private Context context;

	private Double amount;

	private Date startDate;

	private Date endDate;

	private List<StockParameter> parameters = new ArrayList<>();

}
