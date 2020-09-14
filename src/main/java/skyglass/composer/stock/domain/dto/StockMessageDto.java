package skyglass.composer.stock.domain.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import skyglass.composer.stock.domain.model.StockParameter;

@Getter
@Setter
public class StockMessageDto implements Serializable {

	private static final long serialVersionUID = 2798252105646239223L;

	private String itemUuid;

	private String fromUuid;

	private String toUuid;

	private Double amount;

	private Date createdAt;

	private List<StockParameter> stockParameters = new ArrayList<>();

	private String id;

}
