package skyglass.composer.utils.date;

import java.util.Date;

public class DateDayPoint {

	private final int year;

	private final int month;

	private final int day;

	private Date date;

	public DateDayPoint(int year, int month, int day, Date date) {
		this.year = year;
		this.month = month;
		this.day = day;
		this.date = date;
	}

	public int getYear() {
		return year;
	}

	public int getMonth() {
		return month;
	}

	public int getDay() {
		return day;
	}

	public Date getDate() {
		return date;
	}

}
