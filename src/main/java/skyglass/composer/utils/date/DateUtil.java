package skyglass.composer.utils.date;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.StringUtils;

public class DateUtil {

	private static final Map<String, Map<ZoneId, DateTimeFormatter>> formatterMap = new ConcurrentHashMap<>();

	private static final Map<String, ZoneId> zoneIdMap = new ConcurrentHashMap<>();

	private static final Map<String, TimeZone> timeZoneMap = new ConcurrentHashMap<>();

	public static final String DEFAULT_TIME_ZONE = "UTC";

	private static final String DISPLAY_DATE_PATTERN = "yyyy-MM-dd";

	private static final String DISPLAY_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

	private static final String DATE_PATTERN = "yyyy-M-d";

	private static final String DATE_TIME_PATTERN = "yyyy-M-d H:m:s";

	private static final String DATE_TIME_HOUR_MINUTES_PATTERN = "yyyy-MM-dd HH:mm";

	private static final String ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

	public static String getOffsetSeconds(String timezone, Date date) {
		if (StringUtils.isBlank(timezone)) {
			return Integer.toString(0);
		}
		TimeZone tz = TimeZone.getTimeZone(timezone);
		int offset = getOffset(tz, date) / 1000;
		return Integer.toString(offset);
	}

	private static int getOffset(TimeZone tz, Date date) {
		return date == null ? tz.getRawOffset() : tz.getOffset(date.getTime());
	}

	public static DateTimeFormatter getDisplayDateFormatter(String timezone) {
		return getFormatter(DISPLAY_DATE_PATTERN, timezone);
	}

	public static DateTimeFormatter getDisplayDateTimeFormatter(String timezone) {
		return getFormatter(DISPLAY_DATE_TIME_PATTERN, timezone);
	}

	public static DateTimeFormatter getDateTimeFormatter(String timezone) {
		return getFormatter(DATE_TIME_PATTERN, timezone);
	}

	public static DateTimeFormatter getDateFormatter(String timezone) {
		return getFormatter(DATE_PATTERN, timezone);
	}

	public static DateTimeFormatter getFormatter(String pattern, String timezone) {
		ZoneId zoneId = getZoneId(timezone);
		return formatterMap.computeIfAbsent(pattern, p -> new ConcurrentHashMap<>()).computeIfAbsent(zoneId, tz -> DateTimeFormatter.ofPattern(pattern).withZone(tz));
	}

	public static Date now() {
		return now(null);
	}

	public static Date now(String timezone) {
		return toDateTime(LocalDateTime.now(getZoneId(timezone)), timezone);
	}

	public static Date min() {
		return new Date(0);
	}

	public static Date toDateTime(LocalDateTime localDate) {
		return toDateTime(localDate, null);
	}

	public static Date toDateTime(LocalDateTime localDate, String timezone) {
		return Date.from(localDate.atZone(getZoneId(timezone)).toInstant());
	}

	public static LocalDateTime fromDateTime(Date localDate) {
		return fromDateTime(localDate, null);
	}

	public static LocalDateTime fromDateTime(Date localDate, String timezone) {
		return zonedDateTime(localDate, timezone).toLocalDateTime();
	}

	public static ZonedDateTime zonedDateTime(Date localDate, String timezone) {
		return localDate.toInstant().atZone(getZoneId(timezone));
	}

	public static Date toDate(LocalDate localDate) {
		return toDate(localDate, DEFAULT_TIME_ZONE);
	}

	public static Date toDate(LocalDate localDate, String timezone) {
		return Date.from(localDate.atStartOfDay(getZoneId(timezone)).toInstant());
	}

	public static LocalDate fromDate(Date date) {
		return fromDate(date, null);
	}

	public static LocalDate fromDate(Date date, String timezone) {
		return date.toInstant().atZone(getZoneId(timezone)).toLocalDate();
	}

	public static ZoneId getDefaultZoneId() {
		return getZoneId(DEFAULT_TIME_ZONE);
	}

	public static ZoneId getZoneId(String timezone) {
		return StringUtils.isBlank(timezone) ? getDefaultZoneId() : zoneIdMap.computeIfAbsent(timezone, key -> getTimeZone(timezone).toZoneId());
	}

	public static TimeZone getDefaultTimeZone() {
		return getTimeZone(DEFAULT_TIME_ZONE);
	}

	public static TimeZone getTimeZone(String timezone) {
		return StringUtils.isBlank(timezone) ? getDefaultTimeZone() : timeZoneMap.computeIfAbsent(timezone, key -> TimeZone.getTimeZone(timezone));
	}

	public static DayOfWeek getDayOfWeek(Date date, String timeZone) {
		return fromDate(date, timeZone).getDayOfWeek();
	}

	public static int getDaysInPeriod(Date fromDate, Date toDate, String timezone) {
		int result = (int) ChronoUnit.DAYS.between(fromDateTime(fromDate, timezone), fromDateTime(toDate, timezone));
		return result < 0 ? 0 : result;
	}

	public static int getWeeksInPeriod(Date fromDate, Date toDate, String timezone) {
		int result = (int) ChronoUnit.WEEKS.between(fromDateTime(fromDate, timezone), fromDateTime(toDate, timezone));
		return result < 0 ? 0 : result;
	}

	public static int getSecondsInPeriod(Date fromDate, Date toDate, String timezone) {
		int result = (int) ChronoUnit.SECONDS.between(fromDateTime(fromDate, timezone), fromDateTime(toDate, timezone));
		return result < 0 ? 0 : result;
	}

	public static Date plusWeeks(Date date, int weeks, String timezone) {
		return toDateTime(fromDateTime(date, timezone).plusWeeks(weeks), timezone);
	}

	public static Date minusWeeks(Date date, int weeks, String timezone) {
		return toDateTime(fromDateTime(date, timezone).minusWeeks(weeks), timezone);
	}

	public static Date plusSeconds(Date date, int days, String timezone) {
		return toDateTime(fromDateTime(date, timezone).plusSeconds(days), timezone);
	}

	public static Date plusNanos(Date date, int nanos, String timezone) {
		return toDateTime(fromDateTime(date, timezone).plusNanos(nanos), timezone);
	}

	public static Date plusDays(Date date, int days, String timezone) {
		return toDateTime(fromDateTime(date, timezone).plusDays(days), timezone);
	}

	public static Date plusMonths(Date date, int months, String timezone) {
		return toDateTime(fromDateTime(date, timezone).plusMonths(months), timezone);
	}

	public static Date minusSeconds(Date date, int days, String timezone) {
		return toDateTime(fromDateTime(date, timezone).minusSeconds(days), timezone);
	}

	public static Date minusDays(Date date, int days, String timezone) {
		return toDateTime(fromDateTime(date, timezone).minusDays(days), timezone);
	}

	public static Date plusHours(Date date, int hours, String timezone) {
		return toDateTime(fromDateTime(date, timezone).plusHours(hours), timezone);
	}

	public static Date minusHours(Date date, int hours, String timezone) {
		return toDateTime(fromDateTime(date, timezone).minusHours(hours), timezone);
	}

	public static Date endOfDayDate(Date date, String timezone) {
		return toDateTime(fromDate(date, timezone).atTime(23, 59, 59), timezone);
	}

	public static Date startOfDayDate(Date date, String timezone) {
		return toDateTime(fromDate(date, timezone).atStartOfDay(), timezone);
	}

	public static Date startOfMonthDate(int year, int month, String timezone) {
		return toDate(startOfMonthLocalDate(LocalDate.of(year, month, 1), timezone));
	}

	public static Date startOfWeekDate(int year, int week, String timezone) {
		return toDate(startOfWeekLocalDate(LocalDate.of(year, 1, 1).with(WeekFields.ISO.weekOfYear(), week), timezone));
	}

	public static Date startOfNextDayDate(Date date, String timezone) {
		return toDateTime(fromDate(date, timezone).atStartOfDay().plusDays(1), timezone);
	}

	public static Date startOfMonthDate(Date date, String timezone) {
		LocalDate localDate = DateUtil.fromDate(date, timezone);
		return toDate(startOfMonthLocalDate(localDate, timezone));
	}

	public static Date startOfWeekDate(Date date, String timezone) {
		LocalDate localDate = DateUtil.fromDate(date, timezone);
		return toDate(startOfWeekLocalDate(localDate, timezone));
	}

	public static LocalDate startOfMonthLocalDate(LocalDate localDate, String timezone) {
		return localDate.withYear(localDate.getYear())
				.withMonth(localDate.getMonthValue())
				.withDayOfMonth(1).atStartOfDay(DateUtil.getZoneId(timezone)).toLocalDate();
	}

	public static LocalDate startOfWeekLocalDate(LocalDate localDate, String timezone) {
		return localDate.withYear(localDate.getYear())
				.with(WeekFields.ISO.weekOfYear(), localDate.get(WeekFields.ISO.weekOfWeekBasedYear()))
				.with(WeekFields.ISO.dayOfWeek(), 1).atStartOfDay(DateUtil.getZoneId(timezone)).toLocalDate();
	}

	public static Date startOfNextMonthDate(Date date, String timezone) {
		LocalDate localDate = DateUtil.fromDate(date, timezone);
		return toDate(startOfMonthLocalDate(localDate, timezone).plusMonths(1), timezone);
	}

	public static Collection<DateMonthPeriod> getDateMonthPeriods(Date fromDate, Date toDate, String timezone) {
		Collection<DateMonthPeriod> dateMonthPeriods = new ArrayList<>();
		LocalDateTime currentDate = fromDateTime(fromDate, timezone);
		LocalDateTime currentMonthStart = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), 1).atStartOfDay(getZoneId(timezone)).toLocalDateTime();
		do {
			LocalDateTime monthStart = currentMonthStart;
			LocalDateTime monthEnd = monthStart.plusMonths(1);
			dateMonthPeriods.add(new DateMonthPeriod(monthStart.getYear(), monthStart.getMonthValue(),
					DateUtil.toDateTime(monthStart, timezone), DateUtil.toDateTime(monthEnd, timezone)));
			currentMonthStart = monthEnd;
		} while (DateUtil.toDateTime(currentMonthStart, timezone).before(toDate));

		return dateMonthPeriods;
	}

	public static DateDayPoint getDateTimePoint(Date date, String timezone) {
		LocalDateTime currentDate = DateUtil.fromDateTime(date, timezone);
		LocalDateTime currentDayStart = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),
				currentDate.getDayOfMonth()).atStartOfDay(getZoneId(timezone)).toLocalDateTime();
		return new DateDayPoint(currentDayStart.getYear(), currentDayStart.getMonthValue(),
				currentDayStart.getDayOfMonth(), DateUtil.toDateTime(currentDayStart, timezone));
	}

	public static Collection<DateDayPeriod> getDateDayPeriods(Date fromDate, Date toDate, String timezone) {
		toDate = toDateTime(fromDate(toDate, timezone).atStartOfDay(getZoneId(timezone)).plusDays(1).toLocalDateTime(), timezone);
		Collection<DateDayPeriod> dateDayPeriods = new ArrayList<>();
		LocalDateTime currentDate = DateUtil.fromDateTime(fromDate, timezone);
		LocalDateTime currentDayStart = LocalDate.of(currentDate.getYear(), currentDate.getMonth(),
				currentDate.getDayOfMonth()).atStartOfDay(getZoneId(timezone)).toLocalDateTime();
		do {
			LocalDateTime dayStart = currentDayStart;
			LocalDateTime dayEnd = dayStart.plusDays(1);
			dateDayPeriods.add(new DateDayPeriod(dayStart.getYear(), dayStart.getMonthValue(),
					dayStart.getDayOfMonth(), DateUtil.toDateTime(dayStart, timezone), DateUtil.toDateTime(dayEnd, timezone)));
			currentDayStart = dayEnd;
		} while (DateUtil.toDateTime(currentDayStart, timezone).before(toDate));

		return dateDayPeriods;
	}

	public static String format(Date date, String timezone) {
		return getDisplayDateFormatter(timezone).format(fromDate(date, timezone));
	}

	public static String formatToDateTime(Date dateTime, String timezone) {
		return getDisplayDateTimeFormatter(timezone).format(fromDateTime(dateTime, timezone));
	}

	public static String format(Date date, String pattern, String timezone) {
		return getFormatter(pattern, timezone).format(fromDateTime(date, timezone));
	}

	public static Date parseEndOfDayDate(String dateString) {
		return parseEndOfDayDate(dateString, null);
	}

	public static Date parseEndOfDayDate(String dateString, String timezone) {
		return endOfDayDate(parse(dateString, timezone), timezone);
	}

	public static Date parseNextDayDate(String dateString, String timezone) {
		return startOfNextDayDate(parse(dateString, timezone), timezone);
	}

	public static Date parseNextMonthDate(String dateString, String timezone) {
		return startOfNextMonthDate(parse(dateString, timezone), timezone);
	}

	public static Date parse(String dateString) {
		return parse(dateString, null);
	}

	public static Date parse(String dateString, String timezone) {
		return toDate(parseToLocalDate(dateString, timezone), timezone);
	}

	public static Date parseDateTimeWithFormatter(String dateString, String pattern) {
		return parseDateTimeWithFormatter(dateString, pattern, null);
	}

	public static Date parseDateTimeWithFormatter(String dateString, String pattern, String timezone) {
		return toDateTime(parseToLocalDateTimeWithFormatter(dateString, pattern, timezone), timezone);
	}

	public static Date parseDateWithFormatter(String dateString, String pattern) {
		return parseDateWithFormatter(dateString, pattern, null);
	}

	public static Date parseDateWithFormatter(String dateString, String pattern, String timezone) {
		return toDate(parseToLocalDateWithFormatter(dateString, pattern, timezone), timezone);
	}

	public static LocalDateTime parseToLocalDateTimeWithFormatter(String dateString, String pattern) {
		return parseToLocalDateTimeWithFormatter(dateString, pattern, null);
	}

	public static LocalDateTime parseToLocalDateTimeWithFormatter(String dateString, String pattern, String timezone) {
		return LocalDateTime.parse(dateString, getFormatter(pattern, timezone));
	}

	public static LocalDate parseToLocalDateWithFormatter(String dateString, String pattern) {
		return parseToLocalDateWithFormatter(dateString, pattern, null);
	}

	public static LocalDate parseToLocalDateWithFormatter(String dateString, String pattern, String timezone) {
		return LocalDate.parse(dateString, getFormatter(pattern, timezone));
	}

	public static LocalDate parseToLocalDate(String dateString) {
		return parseToLocalDate(dateString, null);
	}

	public static LocalDate parseToLocalDate(String dateString, String timezone) {
		return LocalDate.parse(dateString, getDateFormatter(timezone));
	}

	public static LocalDateTime parseToLocalDateTime(String dateTimeString) {
		return parseToLocalDateTime(dateTimeString, null);
	}

	public static LocalDateTime parseToLocalDateTime(String dateTimeString, String timezone) {
		return LocalDateTime.parse(dateTimeString, getDateTimeFormatter(timezone));
	}

	public static Date parseDateTime(String dateTimeString) {
		return parseDateTime(dateTimeString, null);
	}

	public static Date parseDateTime(String dateTimeString, String timezone) {
		return toDateTime(parseToLocalDateTime(dateTimeString, timezone), timezone);
	}

	public static Date parseHourMinutes(Date date, String hourMinutes, String timezone) {
		String dateString = format(date, timezone);
		return parseDateTimeWithFormatter(dateString + " " + hourMinutes, DATE_TIME_HOUR_MINUTES_PATTERN, timezone);
	}

	public static Date convertTimeStampToDate(Object javaSqlTimeStamp) {
		return (Date) javaSqlTimeStamp;
	}

	public static Date parseIsoDate(String text) {
		return Date.from(OffsetDateTime.parse(text).toInstant());
	}

	public static String formatIsoDate(Date date, String timezone) {
		return DateTimeFormatter.ofPattern(ISO_DATE_TIME_PATTERN).format(fromDateTime(date, timezone));
	}

	public static String formatIsoDateFromString(String text, String timezone) {
		return DateTimeFormatter.ofPattern(ISO_DATE_TIME_PATTERN).format(zonedDateTime(parseDateTime(text, timezone), timezone));
	}

	public static Timestamp toTimestamp(Date date) {
		return Timestamp.valueOf(DateUtil.fromDateTime(date, DEFAULT_TIME_ZONE));
	}

	public static Timestamp toTimestamp(Date date, String timezone) {
		return Timestamp.valueOf(DateUtil.fromDateTime(date, timezone));
	}

}
