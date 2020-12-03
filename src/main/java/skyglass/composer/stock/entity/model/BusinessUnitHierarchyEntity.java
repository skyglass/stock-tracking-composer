package skyglass.composer.stock.entity.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "businessunithierarchy")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BusinessUnitHierarchyEntity extends AEntity {

	private static final long serialVersionUID = 8750966701381584635L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@ManyToOne
	private BusinessUnitEntity child;

	@ManyToOne
	private BusinessUnitEntity parent;

}
