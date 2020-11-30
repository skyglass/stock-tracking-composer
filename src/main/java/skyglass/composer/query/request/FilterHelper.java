package skyglass.composer.query.request;

import java.util.Date;

import skyglass.composer.query.filter.DateColumnFilter;

public class FilterHelper {

	public static FilterModel createDateFilter(ColumnId columnId, Date from, Date to) {
		return new FilterModel(new ColumnVO(columnId, AggFunc.none),
				new DateColumnFilter(FilterType.greaterThanOrEqual, from, to));
	}

}
