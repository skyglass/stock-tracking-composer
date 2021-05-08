
package skyglass.composer.sensor.domain.model;

import java.util.Date;

import skyglass.composer.stock.domain.dto.AEntityDTO;

public class SensorValueParameterDTO extends AEntityDTO {

	private static final long serialVersionUID = -6996642001340404382L;

	private String name;

	private String reference;

	private String value;

	private Date createdAt;

	private String createdBy;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}

}
