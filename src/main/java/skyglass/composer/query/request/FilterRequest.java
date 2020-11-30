package skyglass.composer.query.request;

import java.util.Map;

import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

import skyglass.composer.query.filter.ColumnFilter;

public class FilterRequest {

	private Map<ColumnId, ColumnFilter> filterModel;

	public FilterRequest() {
	}

	public FilterRequest(Map<ColumnId, ColumnFilter> filterModel) {
		this.filterModel = filterModel;
	}

	public Map<ColumnId, ColumnFilter> getFilterModel() {
		return filterModel;
	}

	public void setFilterModel(Map<ColumnId, ColumnFilter> filterModel) {
		this.filterModel = filterModel;
	}

	@Override
	public String toString() {
		return new ReflectionToStringBuilder(this).build();
	}
}
