package skyglass.composer.stock.domain.dto;

import io.swagger.annotations.ApiModel;

@ApiModel(parent = AEntityDTO.class)
public class AEntityNameDTO extends AEntityDTO {
	private static final long serialVersionUID = 1L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
