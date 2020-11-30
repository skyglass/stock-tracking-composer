package skyglass.composer.query.filter;

import java.util.Arrays;
import java.util.Date;

import skyglass.composer.query.request.FilterType;

public class DateColumnFilter extends ColumnFilter {
	private FilterType type;

	private Date filter;

	private Date filterTo;

	public DateColumnFilter() {
		super(null);
	}

	public DateColumnFilter(FilterType type, Date filter, Date filterTo) {
		super(Arrays.asList(filter, filterTo));
		this.type = type;
		this.filter = filter;
		this.filterTo = filterTo;
	}

	public FilterType getType() {
		return type;
	}

	public Date getFilter() {
		return filter;
	}

	public Date getFilterTo() {
		return filterTo;
	}
}
