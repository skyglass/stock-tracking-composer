package skyglass.composer.stock.entity.model;

import java.util.Objects;

import javax.persistence.MappedSuperclass;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import skyglass.composer.stock.domain.model.IdObject;

/**
 * Abstract base class for all entities.
 *
 */
@MappedSuperclass
public abstract class AEntity implements IdObject {

	private static final long serialVersionUID = -4895128247398446344L;

	@Override
	public int hashCode() {
		return Objects.hashCode(this.getUuid());
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
		return Objects.equals(this.getUuid(), other.getUuid());
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).build();
	}

}
