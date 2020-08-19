package skyglass.composer.utils.date;

import java.util.Date;

public class DateDayPeriod {

	private int year;

	private int month;

	private int day;

	private Date dateFrom;

	private Date dateTo;

	public DateDayPeriod(int year, int month, int day, Date dateFrom, Date dateTo) {
		this.year = year;
		this.month = month;
		this.day = day;
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

	public int getDay() {
		return day;
	}

}
