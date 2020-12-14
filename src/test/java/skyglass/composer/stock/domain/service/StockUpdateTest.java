package skyglass.composer.stock.domain.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.service.ContextService;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.model.StockHistory;
import skyglass.composer.stock.entity.repository.StockTransactionRepository;
import skyglass.composer.stock.entity.service.ItemService;
import skyglass.composer.stock.entity.service.StockHistoryService;
import skyglass.composer.stock.entity.service.StockMessageService;
import skyglass.composer.stock.entity.service.StockService;
import skyglass.composer.stock.test.helper.StockBookingTestHelper;
import skyglass.composer.test.config.TestDataConstants;
import skyglass.composer.test.config.TestDateUtil;
import skyglass.composer.test.util.AsyncTestUtil;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class StockUpdateTest extends AbstractAsyncStockUpdateTest {

	@Autowired
	private StockBookingService stockBookingService;

	@Autowired
	private ContextService contextService;

	@Autowired
	private StockService stockService;

	@Autowired
	private StockHistoryService stockHistoryService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private StockMessageService stockMessageService;

	@Autowired
	private StockTransactionRepository stockTransactionBean;

	private StockBookingTestHelper stockBookingTestHelper;

	private Context stockCenter;

	private Context existingContext;

	private Context existingContext2;

	private Item existingItem;

	@Before
	public void init() {
		stockBookingTestHelper = StockBookingTestHelper.create(stockBookingService);
		stockCenter = contextService.getByUuid(TestDataConstants.STOCK_CENTER_UUID);
		existingContext = contextService.getByUuid(TestDataConstants.CONTEXT1_UUID);
		existingContext2 = contextService.getByUuid(TestDataConstants.CONTEXT2_UUID);
		existingItem = itemService.getByUuid(TestDataConstants.ITEM1_UUID);
	}

	@Test
	public void bookingTest() throws InterruptedException, ExecutionException {

		setupInitialStock();

		AsyncTestUtil.pollResult(2, () -> stockMessageService.getAll().size());
		AsyncTestUtil.pollResult(0, () -> stockTransactionBean.getPendingTransactionsCount());

		setupStock();
		invokeAll();

		AsyncTestUtil.pollResult(6, () -> stockMessageService.getAll().size());
		AsyncTestUtil.pollResult(0, () -> stockTransactionBean.getPendingTransactionsCount());

		setupInvalidStock();

		AsyncTestUtil.pollResult(9, () -> stockMessageService.getAll().size());
		AsyncTestUtil.pollResult(0, () -> stockTransactionBean.getPendingTransactionsCount());

		Stock stock = stockService.findByItemAndContext(existingItem.getUuid(), stockCenter.getUuid());
		checkStock(stock, stockCenter, -400D);
		stock = stockService.findByItemAndContext(existingItem.getUuid(), existingContext.getUuid());
		checkStock(stock, existingContext, 200D);
		stock = stockService.findByItemAndContext(existingItem.getUuid(), existingContext2.getUuid());
		checkStock(stock, existingContext2, 200D);

		List<StockHistory> history = stockHistoryService.find(existingItem, stockCenter);
		checkStockHistory(history, stockCenter, 0D, TestDateUtil.parseDateTime("2017-11-01 00:00:00"));
		checkStockHistory(history, stockCenter, -100D, TestDateUtil.parseDateTime("2017-11-01 05:00:00"));
		checkStockHistory(history, stockCenter, -200D, TestDateUtil.parseDateTime("2017-11-02 00:00:00"));
		checkStockHistory(history, stockCenter, -300D, TestDateUtil.parseDateTime("2017-11-03 00:00:00"));
		checkStockHistory(history, stockCenter, -400D, TestDateUtil.parseDateTime("2017-11-04 00:00:00"));
		checkStockHistory(history, stockCenter, -400D, TestDateUtil.parseDateTime("2017-11-05 00:00:00"));
		checkStockHistory(history, stockCenter, -400D, TestDateUtil.parseDateTime("2017-11-06 00:00:00"));

		history = stockHistoryService.find(existingItem, existingContext);
		checkStockHistory(history, existingContext, 0D, TestDateUtil.parseDateTime("2017-11-01 00:00:00"));
		checkStockHistory(history, existingContext, 100D, TestDateUtil.parseDateTime("2017-11-01 05:00:00"));
		checkStockHistory(history, existingContext, 100D, TestDateUtil.parseDateTime("2017-11-02 00:00:00"));
		checkStockHistory(history, existingContext, 200D, TestDateUtil.parseDateTime("2017-11-03 00:00:00"));
		checkStockHistory(history, existingContext, 200D, TestDateUtil.parseDateTime("2017-11-04 00:00:00"));
		checkStockHistory(history, existingContext, 197D, TestDateUtil.parseDateTime("2017-11-05 00:00:00"));
		checkStockHistory(history, existingContext, 200D, TestDateUtil.parseDateTime("2017-11-06 00:00:00"));
		checkStockHistory(history, existingContext, 200D, TestDateUtil.parseDateTime("2017-11-07 00:00:00"));
		checkStockHistory(history, existingContext, 200D, TestDateUtil.parseDateTime("2017-11-08 00:00:00"));

		history = stockHistoryService.find(existingItem, existingContext2);
		checkStockHistory(history, existingContext2, 0D, TestDateUtil.parseDateTime("2017-11-01 00:00:00"));
		checkStockHistory(history, existingContext2, 0D, TestDateUtil.parseDateTime("2017-11-01 05:00:00"));
		checkStockHistory(history, existingContext2, 100D, TestDateUtil.parseDateTime("2017-11-02 00:00:00"));
		checkStockHistory(history, existingContext2, 100D, TestDateUtil.parseDateTime("2017-11-03 00:00:00"));
		checkStockHistory(history, existingContext2, 200D, TestDateUtil.parseDateTime("2017-11-04 00:00:00"));
		checkStockHistory(history, existingContext2, 203D, TestDateUtil.parseDateTime("2017-11-05 00:00:00"));
		checkStockHistory(history, existingContext2, 200D, TestDateUtil.parseDateTime("2017-11-06 00:00:00"));
		checkStockHistory(history, existingContext2, 200D, TestDateUtil.parseDateTime("2017-11-07 00:00:00"));
		checkStockHistory(history, existingContext2, 200D, TestDateUtil.parseDateTime("2017-11-08 00:00:00"));
	}

	private void setupInitialStock() {
		stockBookingTestHelper.createStockMessage(existingItem, stockCenter, existingContext, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-01 00:00:01")));
		stockBookingTestHelper.createStockMessage(existingItem, stockCenter, existingContext2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-01 05:00:01")));
	}

	private void setupStock() {
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, stockCenter, existingContext, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-02 00:00:01"))));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, stockCenter, existingContext2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-03 00:00:01"))));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, existingContext, existingContext2, 3D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-04 00:00:01"))));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, existingContext2, existingContext, 3D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-05 00:00:01"))));
	}

	private void setupInvalidStock() throws InterruptedException {
		stockBookingTestHelper.createStockMessage(existingItem, existingContext, existingContext2, 201D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-07 00:00:01")));

		AsyncTestUtil.pollResult(7, () -> stockMessageService.getAll().size());
		AsyncTestUtil.pollResult(0, () -> stockTransactionBean.getPendingTransactionsCount());

		Stock stock = stockService.findByItemAndContext(existingItem.getUuid(), stockCenter.getUuid());
		checkStock(stock, stockCenter, -400D);
		stock = stockService.findByItemAndContext(existingItem.getUuid(), existingContext.getUuid());
		checkStock(stock, existingContext, 200D);
		stock = stockService.findByItemAndContext(existingItem.getUuid(), existingContext2.getUuid());
		checkStock(stock, existingContext2, 200D);

		stockService.deactivate(existingItem.getUuid(), existingContext2.getUuid());
		stockBookingTestHelper.createStockMessage(existingItem, existingContext, existingContext2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-07 00:00:01")));

		stockService.deactivate(existingItem.getUuid(), existingContext.getUuid());
		stockBookingTestHelper.createStockMessage(existingItem, existingContext, existingContext2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-07 00:00:01")));

	}

	private void checkStock(Stock stock, Context context, Double amount) {
		Assert.assertEquals(existingItem.getUuid(), stock.getItem().getUuid());
		Assert.assertEquals(context.getUuid(), stock.getContext().getUuid());
		Assert.assertEquals(amount, stock.getAmount());
	}

	private void checkStockHistory(List<StockHistory> stockHistory, Context context, Double amount, Date validityDate) {
		boolean found = false;
		for (StockHistory stock : stockHistory) {
			Assert.assertEquals(existingItem.getUuid(), stock.getItem().getUuid());
			Assert.assertEquals(context.getUuid(), stock.getContext().getUuid());
			if ((stock.getStartDate() == null || stock.getStartDate().getTime() <= validityDate.getTime()) && (stock.getEndDate() == null || stock.getEndDate().getTime() > validityDate.getTime())) {
				if (found) {
					Assert.fail("Periods must not intersect");
				} else {
					found = true;
					Assert.assertEquals(amount, stock.getAmount());
				}
			}
		}
		if (!found) {
			Assert.fail("Stock was not found");
		}
	}

}
