package skyglass.composer.query.request;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import skyglass.composer.query.filter.ColumnFilter;

public class FilterModel implements Serializable {

	private static final long serialVersionUID = -7228159132852223724L;

	private ColumnVO colId;

	private ColumnFilter filter;

	public FilterModel() {
	}

	public FilterModel(ColumnVO colId, ColumnFilter filter) {
		this.colId = colId;
		this.filter = filter;
	}

	public ColumnVO getColId() {
		return colId;
	}

	public void setColId(ColumnVO colId) {
		this.colId = colId;
	}

	public ColumnFilter getFilter() {
		return filter;
	}

	public void setColumnFilter(ColumnFilter filter) {
		this.filter = filter;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		FilterModel filterModel = (FilterModel) o;

		return new EqualsBuilder()
				.append(colId, filterModel.colId)
				.isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(colId)
				.toHashCode();
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).build();
	}
}
