package skyglass.composer.stock.domain.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author skyglass
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtUserDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private ExtUserNameDTO name;

	private String id;

	private Boolean active;

	public ExtUserDTO() {

	}

	public ExtUserNameDTO getName() {
		return name;
	}

	public void setName(ExtUserNameDTO name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Boolean isActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}
}
