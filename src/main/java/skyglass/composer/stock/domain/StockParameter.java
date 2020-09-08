package skyglass.composer.stock.domain;

import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.persistence.entity.StockParameterEntity;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockParameter extends AObject {

	private static final long serialVersionUID = -5317017516582220570L;

	private String uuid;

	private String name;

	private String value;

	public static List<StockParameter> list(List<StockParameterEntity> list) {
		return list.stream().map(p -> new StockParameter(p.getUuid(), p.getName(), p.getValue())).collect(Collectors.toList());
	}

	public static List<StockParameterEntity> entityList(List<StockParameter> list) {
		return list.stream().map(p -> new StockParameterEntity(p.getUuid(), p.getName(), p.getValue())).collect(Collectors.toList());
	}

	public static List<StockParameterEntity> copyList(List<StockParameterEntity> list) {
		return list.stream().map(p -> new StockParameterEntity(null, p.getName(), p.getValue())).collect(Collectors.toList());
	}

}
