package skyglass.composer.stock.domain.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.domain.repository.StockTransactionBean;

@Component
public class StockBookingScheduler {

	@Autowired
	private StockBookingAsyncTask stockBookingAsyncTask;

	@Autowired
	private StockTransactionBean stockTransactionBean;

	// runs every 5 seconds
	@Scheduled(cron = "0/5 * * * * *")
	public void executeSchedulerTask() {
		stockTransactionBean.deleteCommittedTransactions();
		List<StockMessage> stockMessages = stockTransactionBean.findPendingMessages();
		stockMessages.stream().forEach(s -> stockBookingAsyncTask.task(s));
	}

}
