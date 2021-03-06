/**
 *
 */
package skyglass.composer.security.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import skyglass.composer.security.domain.model.IUser;
import skyglass.composer.stock.domain.dto.AEntityDTO;

/**
 * @author skyglass
 *
 */
@ApiModel(parent = AEntityDTO.class)
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDTO extends AEntityDTO implements IUser {

	private static final long serialVersionUID = 1L;

	private String email;

	private String username;

	private String firstName;

	private String lastName;

	private String ownerUuid;

	private String ownerName;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getOwnerUuid() {
		return ownerUuid;
	}

	public void setOwnerUuid(String ownerUuid) {
		this.ownerUuid = ownerUuid;
	}

	public String getOwnerName() {
		return ownerName;
	}

	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}

}
