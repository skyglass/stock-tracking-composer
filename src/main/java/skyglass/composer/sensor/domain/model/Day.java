package skyglass.composer.sensor.domain.model;

import java.time.DayOfWeek;

/**
 * @author ajaykumar
 */
public enum Day {
	//don't change the ordering as it is used in getSummary for machineScheduleItems
	SUNDAY(DayOfWeek.SUNDAY), MONDAY(DayOfWeek.MONDAY), TUESDAY(DayOfWeek.TUESDAY), WEDNESDAY(DayOfWeek.WEDNESDAY), THURSDAY(DayOfWeek.THURSDAY), FRIDAY(DayOfWeek.FRIDAY), SATURDAY(
			DayOfWeek.SATURDAY);

	private final DayOfWeek dayOfWeek;

	private Day(DayOfWeek dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	public DayOfWeek getDayOfWeek() {
		return dayOfWeek;
	}

	public static Day getDay(DayOfWeek dayOfWeek) {
		for (Day value : values()) {
			if (value.getDayOfWeek() == dayOfWeek) {
				return value;
			}
		}
		return null;
	}

	public static Day getNextDay(Day currentDay) {
		if (currentDay == SATURDAY) {
			return SUNDAY;
		}
		return values()[currentDay.ordinal() + 1];
	}
}
