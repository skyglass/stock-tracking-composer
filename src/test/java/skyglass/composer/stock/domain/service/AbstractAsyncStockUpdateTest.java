package skyglass.composer.stock.domain.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.domain.dto.StockMessageDto;
import skyglass.composer.stock.test.reset.AbstractBaseTest;

public abstract class AbstractAsyncStockUpdateTest extends AbstractBaseTest {

	protected ExecutorService threadPool = Executors.newFixedThreadPool(10);

	protected List<Callable<Void>> tasks = new ArrayList<>();

	protected List<StockMessageDto> dtos = new ArrayList<>();

	@Autowired
	protected StockBookingService stockBookingService;

	protected void invokeAll() throws InterruptedException, ExecutionException {
		for (StockMessageDto dto : dtos) {
			Callable<Void> callable = new Callable<Void>() {
				@Override
				public Void call() throws Exception {
					stockBookingService.createStockMessage(dto);
					return null;
				}
			};

			tasks.add(callable);
		}

		Collections.shuffle(tasks);
		List<Future<Void>> futures = threadPool.invokeAll(tasks);
		for (Future<Void> future : futures) {
			future.get();
		}

		shutdown();
	}

	protected void shutdown() {
		threadPool.shutdown();

		try {
			if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
				threadPool.shutdownNow();
			}
		} catch (InterruptedException ex) {
			threadPool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}

}
