package skyglass.composer.query.request;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class ColumnVO implements Serializable {

	private static final long serialVersionUID = -7185407746340157653L;

	private ColumnId id;

	private AggFunc aggFunc;

	public ColumnVO() {
	}

	public ColumnVO(ColumnId id, AggFunc aggFunc) {
		this.id = id;
		this.aggFunc = aggFunc;
	}

	public ColumnId getId() {
		return id;
	}

	public void setId(ColumnId id) {
		this.id = id;
	}

	public AggFunc getAggFunc() {
		return aggFunc;
	}

	public void setAggFunc(AggFunc aggFunc) {
		this.aggFunc = aggFunc;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		ColumnVO columnVO = (ColumnVO) o;

		return new EqualsBuilder()
				.append(id, columnVO.id)
				.append(aggFunc, columnVO.aggFunc)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(id)
				.append(aggFunc)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).build();
	}
}
