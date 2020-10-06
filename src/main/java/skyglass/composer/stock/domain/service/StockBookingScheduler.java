package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockBookingScheduler {

	@Autowired
	private StockBookingAsyncTask stockBookingAsyncTask;

	// runs every 5 seconds
	@Scheduled(cron = "0/5 * * * * *")
	public void executeSchedulerTask() {
		int i = 7;
		while (i-- > 0) {
			stockBookingAsyncTask.task();
		}
	}

}
