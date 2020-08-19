package skyglass.composer.stock.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import skyglass.composer.stock.domain.StockParameter;

public class StockMessageDto implements Serializable {

	private static final long serialVersionUID = 2798252105646239223L;

	private String itemUuid;

	private String fromUuid;

	private String toUuid;

	private Double amount;

	private List<StockParameter> stockParameters = new ArrayList<>();

	private String id;

	public String getItemUuid() {
		return itemUuid;
	}

	public void setItemUuid(String itemUuid) {
		this.itemUuid = itemUuid;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public List<StockParameter> getStockParameters() {
		return stockParameters;
	}

	public void setStockParameters(List<StockParameter> stockParameters) {
		this.stockParameters = stockParameters;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFromUuid() {
		return fromUuid;
	}

	public void setFromUuid(String fromUuid) {
		this.fromUuid = fromUuid;
	}

	public String getToUuid() {
		return toUuid;
	}

	public void setToUuid(String toUuid) {
		this.toUuid = toUuid;
	}

}
