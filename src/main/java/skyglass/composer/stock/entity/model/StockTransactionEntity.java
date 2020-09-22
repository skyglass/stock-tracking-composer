package skyglass.composer.stock.entity.model;

import java.util.Date;

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

@Entity
@Table(name = "stocktransaction")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class StockTransactionEntity extends AEntity {

	private static final long serialVersionUID = 2280715366097558839L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@ManyToOne(optional = false)
	private StockMessageEntity message;

	@Column(nullable = false)
	private Date createdAt;

	@Column
	private boolean pending;

	@Column
	private boolean canceled;

	public void setPending(boolean pending) {
		this.pending = pending;
	}

}
