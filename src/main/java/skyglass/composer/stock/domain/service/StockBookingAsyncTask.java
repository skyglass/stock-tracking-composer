package skyglass.composer.stock.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class StockBookingAsyncTask {

	@Autowired
	private StockUpdateService stockUpdateService;

	@Async
	public void task() {
		stockUpdateService.replayTransactions();
	}

}
