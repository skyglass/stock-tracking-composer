package skyglass.composer.security.entity.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.entity.model.AEntity;

@Entity
@Table(name = "context")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ContextEntity extends AEntity {

	private static final long serialVersionUID = 8750966701381584635L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@Column(nullable = false)
	private String name;

	@ManyToOne
	private OwnerEntity owner;

	@ManyToOne
	private ContextEntity parent;

	@Column(nullable = false)
	private int level;

}
