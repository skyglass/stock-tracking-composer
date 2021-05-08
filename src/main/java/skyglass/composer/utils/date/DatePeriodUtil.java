package skyglass.composer.utils.date;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;

import skyglass.composer.stock.domain.model.IPeriod;

public class DatePeriodUtil {

	/**
	 * Partitions set of periods into set of non-overlapping periods, which produce full period (fromDat, toDate), when combined together
	 * This method is useful when set of periods is given, and each period value is constant during correspondent period.
	 * In this case, we need to return new set of (possibly smaller) non-overlapping periods, where each aggregated period value is constant during correspondent period.
	 * For example, if we have the plan figure, which is equal to 10 from 1 april to 10 april and plan figure, which is equal to 20 from 5 april to 15 april,
	 * then we split the period from 1 april to 15 april in 3 periods:
	 * 1 april - 5 april: planFigure = 10 / 2 = 5
	 * 5 april - 10 april: planFigure = 10 / 2 + 20 / 2 = 15
	 * 10 april - 15 april: planFigure = 20 / 2 = 10
	 * 
	 * Note: If there are any gaps between given periods, then the new periods will fill these gaps (periods between gaps are treated as zero value periods)
	 * 
	 */
	public static <T extends IPeriod> Collection<DatePeriod> splitDatePeriods(Collection<T> periods) {
		return splitDatePeriods(null, null, periods);
	}

	public static <T extends IPeriod> Collection<DatePeriod> splitDatePeriods(Date fromDate, Date toDate,
			Collection<T> periods) {
		Collection<Long> times = new TreeSet<>();
		if (CollectionUtils.isNotEmpty(periods)) {
			times.addAll(periods.stream()
					.filter(period -> (fromDate == null || period.getStartDate().after(fromDate))
							&& (toDate == null || period.getStartDate().before(toDate)))
					.map(period -> period.getStartDate().getTime())
					.collect(Collectors.toSet()));
			times.addAll(periods.stream()
					.filter(period -> (fromDate == null || period.getEndDate().after(fromDate))
							&& (toDate == null || period.getEndDate().before(toDate)))
					.map(period -> period.getEndDate().getTime())
					.collect(Collectors.toSet()));
		}

		Collection<DatePeriod> result = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(times)) {
			int i = 0;
			Date currentDate = null;
			for (Long time : times) {
				Date date = new Date(time);
				if (i == 0 && fromDate != null) {
					result.add(new DatePeriod(fromDate, date, isBreakInterval(fromDate, date, periods)));
				} else if (i > 0) {
					result.add(new DatePeriod(currentDate, date, isBreakInterval(currentDate, date, periods)));
				}
				currentDate = date;
				i++;
			}
			if (toDate != null) {
				result.add(new DatePeriod(currentDate, toDate, isBreakInterval(currentDate, toDate, periods)));
			}
		} else {
			if (fromDate != null && toDate != null) {
				result.add(new DatePeriod(fromDate, toDate, isBreakInterval(fromDate, toDate, periods)));
			}
		}

		return result;
	}

	public static <T extends IPeriod> Collection<DatePeriod> getNonOverlappingPeriods(Collection<T> periods) {
		return combineAdjacentPeriods(splitDatePeriods(null, null, periods).stream().filter(p -> !p.isBreakInterval()).collect(Collectors.toList()));
	}

	private static <T extends IPeriod> Collection<DatePeriod> combineAdjacentPeriods(Collection<DatePeriod> periods) {
		Collection<DatePeriod> result = new ArrayList<>();
		DatePeriod previous = null;
		for (DatePeriod period : periods) {
			if (previous == null) {
				previous = period;
			} else {
				if (previous.getEndDate().getTime() >= period.getStartDate().getTime()) {
					previous = new DatePeriod(previous.getStartDate(), period.getEndDate(), false);
				} else {
					result.add(previous);
					previous = period;
				}
			}
		}
		if (previous != null) {
			result.add(previous);
		}
		return result;
	}

	private static <T extends IPeriod> boolean isBreakInterval(Date startDate, Date endDate, Collection<T> periods) {
		return !isWithinThePeriod(startDate, endDate, periods);
	}

	private static <T extends IPeriod> boolean isWithinThePeriod(Date startDate, Date endDate, Collection<T> periods) {
		for (T period : periods) {
			if (startDate.getTime() >= period.getStartDate().getTime() && endDate.getTime() <= period.getEndDate().getTime()) {
				return true;
			}
		}
		return false;
	}

}
