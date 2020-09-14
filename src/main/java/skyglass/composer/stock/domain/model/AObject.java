package skyglass.composer.stock.domain.model;

import java.util.Objects;

/**
 * Abstract base class for all domain object.
 *
 */
public abstract class AObject implements IdObject {

	private static final long serialVersionUID = -7247763056488390768L;

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
		AObject other = (AObject) obj;
		return Objects.equals(this.getUuid(), other.getUuid());
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + " " + this.getUuid();
	}

}
