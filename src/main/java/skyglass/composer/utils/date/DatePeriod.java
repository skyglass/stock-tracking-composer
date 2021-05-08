package skyglass.composer.utils.date;

import java.util.Date;

import skyglass.composer.stock.domain.model.IPeriod;

public class DatePeriod implements IPeriod {
	private Date startDate;

	private Date endDate;

	private boolean breakInterval;

	public DatePeriod(Date startDate, Date endDate, boolean breakInterval) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.breakInterval = breakInterval;
	}

	@Override
	public Date getStartDate() {
		return startDate;
	}

	@Override
	public Date getEndDate() {
		return endDate;
	}

	public boolean isBreakInterval() {
		return breakInterval;
	}
}
