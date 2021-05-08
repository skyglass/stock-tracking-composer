package skyglass.composer.sensor.domain.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.tuple.Pair;

import skyglass.composer.sensor.domain.model.Day;
import skyglass.composer.sensor.domain.model.ScheduleItem;
import skyglass.composer.stock.domain.model.IPeriod;
import skyglass.composer.utils.date.DatePeriod;
import skyglass.composer.utils.date.DatePeriodUtil;
import skyglass.composer.utils.date.DateUtil;

public class ScheduleItemHelper {

	public static Pair<List<Date>, List<Date>> getScheduleItemDates(Date start, Date end, String timezone, List<ScheduleItem> items) {
		Collection<DatePeriod> periods = getScheduleItemPeriods(start, end, timezone, items);
		if (CollectionUtils.isEmpty(periods)) {
			return Pair.of(Collections.emptyList(), Collections.emptyList());
		}
		List<Date> startDates = new ArrayList<>();
		List<Date> endDates = new ArrayList<>();
		for (DatePeriod datePeriod : periods) {
			startDates.add(datePeriod.getStartDate());
			endDates.add(datePeriod.getEndDate());
		}
		return Pair.of(startDates, endDates);
	}

	public static Collection<DatePeriod> getScheduleItemPeriods(Date start, Date end, String timezone, List<ScheduleItem> items) {
		if (CollectionUtils.isEmpty(items)) {
			return Collections.emptyList();
		}
		Collection<ScheduleItemDecorator> scheduleItemPeriods = findScheduleItemPeriods(start, end, timezone, items);

		if (CollectionUtils.isEmpty(scheduleItemPeriods)) {
			return Collections.emptyList();
		}

		return DatePeriodUtil.getNonOverlappingPeriods(scheduleItemPeriods);
	}

	public static int getScheduledSecondsInPeriod(Date start, Date end, String timezone, List<ScheduleItem> items) {
		if (CollectionUtils.isEmpty(items)) {
			return DateUtil.getSecondsInPeriod(start, end, timezone);
		}
		Collection<ScheduleItemDecorator> scheduleItemPeriods = findScheduleItemPeriods(start, end, timezone, items);

		if (CollectionUtils.isEmpty(scheduleItemPeriods)) {
			return DateUtil.getSecondsInPeriod(start, end, timezone);
		}

		Collection<DatePeriod> periods = DatePeriodUtil.getNonOverlappingPeriods(scheduleItemPeriods);
		int result = 0;

		for (DatePeriod period : periods) {
			Date scheduleStart = period.getStartDate();
			Date scheduleEnd = period.getEndDate();
			int resultDelta = DateUtil.getSecondsInPeriod((scheduleStart.getTime() >= start.getTime() ? scheduleStart : start),
					(scheduleEnd.getTime() >= end.getTime() ? end : scheduleEnd), timezone);
			if (resultDelta > 0) {
				result += resultDelta;
			}
		}

		return result;
	}

	private static Collection<ScheduleItemDecorator> findScheduleItemPeriods(Date start, Date end, String timezone, List<ScheduleItem> items) {
		Date startTime = start;
		Collection<ScheduleItemDecorator> scheduleItemPeriods = new ArrayList<>();
		do {
			for (ScheduleItem item : items) {
				Day day = Day.getDay(DateUtil.getDayOfWeek(startTime, item.getTimezone()));
				if (item.getDay() == day && isDateWithinValidPeriod(item, startTime)) {
					ScheduleItemDecorator scheduleItem = new ScheduleItemDecorator(item, startTime);
					Date scheduleStart = scheduleItem.getStartDate();
					Date scheduleEnd = scheduleItem.getEndDate();
					if (scheduleStart.getTime() >= end.getTime() || scheduleEnd.getTime() <= start.getTime()) {
						//ignore
					} else {
						scheduleItemPeriods.add(scheduleItem);
					}
				}
			}
			startTime = DateUtil.plusDays(startTime, 1, timezone);
		} while (startTime.getTime() <= end.getTime());
		return scheduleItemPeriods;
	}

	public static Pair<Boolean, List<DatePeriod>> isDateWithinTheSchedule(Date startDate, Date endDate, List<ScheduleItem> items) {
		List<DatePeriod> periods = getSchedulePeriods(startDate, endDate, items);
		if (CollectionUtils.isNotEmpty(periods)) {
			Date scheduleStart = periods.get(0).getStartDate();
			if (scheduleStart.getTime() > startDate.getTime()) {
				return Pair.of(Boolean.FALSE, periods);
			} else {
				return Pair.of(Boolean.TRUE, periods);
			}
		}
		return Pair.of(Boolean.FALSE, periods);
	}

	private static List<DatePeriod> getSchedulePeriods(Date startDate, Date endDate, List<ScheduleItem> items) {
		if (CollectionUtils.isEmpty(items)) {
			return Collections.emptyList();
		}
		Day day = null;
		Date dayDate = null;
		Date previousDayDate = null;
		String timezone = null;
		List<ScheduleItemDecorator> periods = new ArrayList<>();
		//we will need periods for 2 working days, since the current date. If for example current date is Friday and the next working day is Monday, then
		//we need all schedule item periods for Friday and Monday, but only starting from the current date
		do {
			for (ScheduleItem item : items) {
				if (day == null) {
					day = Day.getDay(DateUtil.getDayOfWeek(startDate, item.getTimezone()));
					dayDate = startDate;
					timezone = item.getTimezone();
				}
				if (item.getDay() == day && isDateWithinValidPeriod(item, startDate)) {
					ScheduleItemDecorator scheduleItem = new ScheduleItemDecorator(item, dayDate);
					if (scheduleItem.getEndDate().after(startDate) && scheduleItem.getStartDate().before(endDate)) {
						periods.add(scheduleItem);
					}
				}
			}
			day = Day.getNextDay(day);
			previousDayDate = dayDate;
			dayDate = DateUtil.plusDays(dayDate, 1, timezone);
		} while (previousDayDate.before(endDate));
		if (CollectionUtils.isEmpty(periods)) {
			return Collections.emptyList();
		}
		return new ArrayList<>(DatePeriodUtil.splitDatePeriods(periods));
	}

	public static boolean isDateWithinValidPeriod(ScheduleItem item, Date date) {
		Date validFrom = item.getValidFrom();
		Date validTo = item.getValidTo() == null ? null : DateUtil.startOfNextDayDate(item.getValidTo(), item.getTimezone());
		return (validFrom == null || validFrom.getTime() <= date.getTime())
				&& (validTo == null || validTo.getTime() >= date.getTime());
	}

	private static class ScheduleItemDecorator implements IPeriod {

		private Date startDate;

		private Date endDate;

		public ScheduleItemDecorator(ScheduleItem item, Date scheduleItemDate) {
			this.startDate = DateUtil.parseHourMinutes(scheduleItemDate, item.getStartTime(), item.getTimezone());
			this.endDate = DateUtil.parseHourMinutes(scheduleItemDate, item.getEndTime(), item.getTimezone());
		}

		@Override
		public Date getStartDate() {
			return startDate;
		}

		@Override
		public Date getEndDate() {
			return endDate;
		}
	}

}
