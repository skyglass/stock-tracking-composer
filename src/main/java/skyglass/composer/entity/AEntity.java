package skyglass.composer.entity;

import java.util.Objects;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import skyglass.composer.domain.IdObject;

/**
 * Abstract base class for all entities.
 *
 */
@MappedSuperclass
public abstract class AEntity implements IdObject {

	private static final long serialVersionUID = -4895128247398446344L;

	@Id
	@GeneratedValue
	/**
	 * UUID of the JPA Entity
	 */
	protected String uuid;

	@Override
	public String getUuid() {
		return uuid;
	}

	@Override
	public void setUuid(String id) {
		this.uuid = id;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(this.uuid);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AEntity other = (AEntity) obj;
		return Objects.equals(this.uuid, other.uuid);
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + uuid;
	}

}
