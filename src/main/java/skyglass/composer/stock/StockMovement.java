package skyglass.composer.stock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import skyglass.composer.domain.AObject;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockMovement extends AObject {

	private static final long serialVersionUID = 7924635977183057862L;

	private String uuid;

	private Item item;

	private BusinessUnit from;

	private BusinessUnit to;

	private Double amount;

	private Date createdAt;

	private List<StockParameter> parameters = new ArrayList<>();

}
