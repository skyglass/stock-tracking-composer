package skyglass.composer.stock.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockTransaction extends AObject {

	private static final long serialVersionUID = -3280362793541117831L;

	private String uuid;

	private StockMessage message;

	private Date createdAt;

	private boolean pending;

}
