package skyglass.composer.query.filter;

import java.util.Arrays;

import skyglass.composer.query.request.FilterType;

public class NumberColumnFilter extends ColumnFilter {
	private FilterType type;

	private Integer filter;

	private Integer filterTo;

	public NumberColumnFilter() {
		super(null);
	}

	public NumberColumnFilter(FilterType type, Integer filter, Integer filterTo) {
		super(Arrays.asList(filter, filterTo));
		this.type = type;
		this.filter = filter;
		this.filterTo = filterTo;
	}

	public FilterType getType() {
		return type;
	}

	public Integer getFilter() {
		return filter;
	}

	public Integer getFilterTo() {
		return filterTo;
	}
}
