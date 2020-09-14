/**
 *
 */
package skyglass.composer.stock.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import io.swagger.annotations.ApiModel;
import skyglass.composer.stock.domain.model.IUser;

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

}
