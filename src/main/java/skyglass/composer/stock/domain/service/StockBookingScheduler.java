package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StockBookingScheduler {

	@Autowired
	private StockUpdateService stockUpdateService;

	// runs every 5 seconds
	@Scheduled(cron = "0/5 * * * * *")
	public void executeSchedulerTask() {
		stockUpdateService.replayTransactions();
	}

}
