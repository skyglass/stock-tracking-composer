package skyglass.composer.utils.date;

import java.util.Date;

public class DateMonthPeriod {

	private int year;

	private int month;

	private Date dateFrom;

	private Date dateTo;

	public DateMonthPeriod(int year, int month, Date dateFrom, Date dateTo) {
		this.year = year;
		this.month = month;
		this.dateFrom = dateFrom;
		this.dateTo = dateTo;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public Date getDateFrom() {
		return dateFrom;
	}

	public Date getDateTo() {
		return dateTo;
	}

}
