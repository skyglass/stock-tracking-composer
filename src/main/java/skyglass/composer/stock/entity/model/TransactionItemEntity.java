package skyglass.composer.stock.entity.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import skyglass.composer.stock.domain.model.TransactionType;

@Entity
@Table(name = "transactionitem")
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TransactionItemEntity extends AEntity {

	private static final long serialVersionUID = -3449205149191103160L;

	@Id
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@GeneratedValue(generator = "UUID")
	private String uuid;

	@ManyToOne(optional = false)
	private StockTransactionEntity transaction;

	@Column(nullable = false)
	private String key;

	@Enumerated(EnumType.STRING)
	private TransactionType transactionType;

	@Column(nullable = false)
	private Date createdAt;

	@Column
	private boolean pending;

	public void setPending(boolean pending) {
		this.pending = pending;
	}

}
