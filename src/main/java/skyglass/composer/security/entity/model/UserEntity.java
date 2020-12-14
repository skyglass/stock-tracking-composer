package skyglass.composer.security.entity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.AEntity;

@Entity
@Table(name = "\"USER\"")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserEntity extends AEntity {

	private static final long serialVersionUID = 4335292275528049045L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	//@NotEmpty
	@Size(min = 3, max = 15)
	private String username;

	//@NotEmpty
	@Size(min = 5)
	private String password;

	@Email
	//@NotEmpty
	private String email;

	@Column
	private String name;

	@ManyToOne
	private OwnerEntity owner;

	public void setName(String name) {
		this.name = name;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
