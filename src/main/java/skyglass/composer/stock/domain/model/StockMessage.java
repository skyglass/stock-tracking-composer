package skyglass.composer.stock.domain.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockMessage extends AObject {

	private static final long serialVersionUID = 7924635977183057862L;

	private String uuid;

	private Item item;

	private BusinessUnit from;

	private BusinessUnit to;

	private Double amount;

	private Long offset;

	private Date createdAt;

	private String messageId;

	private List<StockParameter> parameters = new ArrayList<>();

	public boolean isBetweenUnits() {
		return !Objects.equals(from.getUuid(), to.getUuid());
	}

	public boolean shouldUpdateStock() {
		//if business units are the same , then don't need to update the stocks
		return isBetweenUnits();
	}

}
