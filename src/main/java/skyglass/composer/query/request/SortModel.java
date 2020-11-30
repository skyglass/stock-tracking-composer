package skyglass.composer.query.request;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

public class SortModel implements Serializable {

	private static final long serialVersionUID = -7228159132852223724L;

	private ColumnVO colId;

	private SortType sort;

	public SortModel() {
	}

	public SortModel(ColumnVO colId, SortType sort) {
		this.colId = colId;
		this.sort = sort;
	}

	public ColumnVO getColId() {
		return colId;
	}

	public void setColId(ColumnVO colId) {
		this.colId = colId;
	}

	public SortType getSort() {
		return sort;
	}

	public void setSort(SortType sort) {
		this.sort = sort;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		SortModel sortModel = (SortModel) o;

		return new EqualsBuilder()
				.append(colId, sortModel.colId)
				.append(sort, sortModel.sort)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(colId)
				.append(sort)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).build();
	}
}
