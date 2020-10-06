package skyglass.composer.stock.domain.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.Stock;
import skyglass.composer.stock.domain.model.StockHistory;
import skyglass.composer.stock.domain.repository.StockTransactionBean;
import skyglass.composer.stock.entity.service.BusinessUnitService;
import skyglass.composer.stock.entity.service.ItemService;
import skyglass.composer.stock.entity.service.StockHistoryService;
import skyglass.composer.stock.entity.service.StockService;
import skyglass.composer.stock.test.helper.StockBookingTestHelper;
import skyglass.composer.stock.test.reset.AbstractBaseTest;
import skyglass.composer.test.config.TestDataConstants;
import skyglass.composer.test.config.TestDateUtil;
import skyglass.composer.test.util.AsyncTestUtil;

@ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class StockUpdateTest extends AbstractAsyncStockUpdateTest {

	@Autowired
	private StockBookingService stockBookingService;

	@Autowired
	private BusinessUnitService businessUnitService;

	@Autowired
	private StockService stockService;

	@Autowired
	private StockHistoryService stockHistoryService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private StockTransactionBean stockTransactionBean;

	private StockBookingTestHelper stockBookingTestHelper;

	private BusinessUnit stockCenter;

	private BusinessUnit existingBusinessUnit;

	private BusinessUnit existingBusinessUnit2;

	private Item existingItem;

	@Before
	public void init() {
		stockBookingTestHelper = StockBookingTestHelper.create(stockBookingService);
		stockCenter = businessUnitService.getByUuid(TestDataConstants.STOCK_CENTER_UUID);
		existingBusinessUnit = businessUnitService.getByUuid(TestDataConstants.BUSINESSUNIT1_UUID);
		existingBusinessUnit2 = businessUnitService.getByUuid(TestDataConstants.BUSINESSUNIT2_UUID);
		existingItem = itemService.getByUuid(TestDataConstants.ITEM1_UUID);
	}

	@Test
	public void bookingTest() throws InterruptedException, ExecutionException {

		setupStock();

		invokeAll();

		setupInvalidStock();

		AsyncTestUtil.pollResult(0, () -> stockTransactionBean.getPendingTransactionsCount());

		Stock stock = stockService.findByItemAndBusinessUnit(existingItem.getUuid(), stockCenter.getUuid());
		checkStock(stock, stockCenter, -400D);
		stock = stockService.findByItemAndBusinessUnit(existingItem.getUuid(), existingBusinessUnit.getUuid());
		checkStock(stock, existingBusinessUnit, 200D);
		stock = stockService.findByItemAndBusinessUnit(existingItem.getUuid(), existingBusinessUnit2.getUuid());
		checkStock(stock, existingBusinessUnit2, 200D);

		List<StockHistory> history = stockHistoryService.find(existingItem, stockCenter);
		checkStockHistory(history, stockCenter, 0D, TestDateUtil.parseDateTime("2017-11-01 00:00:00"));
		checkStockHistory(history, stockCenter, -100D, TestDateUtil.parseDateTime("2017-11-01 05:00:00"));
		checkStockHistory(history, stockCenter, -200D, TestDateUtil.parseDateTime("2017-11-02 00:00:00"));
		checkStockHistory(history, stockCenter, -300D, TestDateUtil.parseDateTime("2017-11-03 00:00:00"));
		checkStockHistory(history, stockCenter, -400D, TestDateUtil.parseDateTime("2017-11-04 00:00:00"));
		checkStockHistory(history, stockCenter, -400D, TestDateUtil.parseDateTime("2017-11-05 00:00:00"));
		checkStockHistory(history, stockCenter, -400D, TestDateUtil.parseDateTime("2017-11-06 00:00:00"));

		history = stockHistoryService.find(existingItem, existingBusinessUnit);
		checkStockHistory(history, existingBusinessUnit, 0D, TestDateUtil.parseDateTime("2017-11-01 00:00:00"));
		checkStockHistory(history, existingBusinessUnit, 100D, TestDateUtil.parseDateTime("2017-11-01 05:00:00"));
		checkStockHistory(history, existingBusinessUnit, 100D, TestDateUtil.parseDateTime("2017-11-02 00:00:00"));
		checkStockHistory(history, existingBusinessUnit, 200D, TestDateUtil.parseDateTime("2017-11-03 00:00:00"));
		checkStockHistory(history, existingBusinessUnit, 200D, TestDateUtil.parseDateTime("2017-11-04 00:00:00"));
		checkStockHistory(history, existingBusinessUnit, 197D, TestDateUtil.parseDateTime("2017-11-05 00:00:00"));
		checkStockHistory(history, existingBusinessUnit, 200D, TestDateUtil.parseDateTime("2017-11-06 00:00:00"));
		checkStockHistory(history, existingBusinessUnit, 200D, TestDateUtil.parseDateTime("2017-11-07 00:00:00"));
		checkStockHistory(history, existingBusinessUnit, 200D, TestDateUtil.parseDateTime("2017-11-08 00:00:00"));

		history = stockHistoryService.find(existingItem, existingBusinessUnit2);
		checkStockHistory(history, existingBusinessUnit2, 0D, TestDateUtil.parseDateTime("2017-11-01 00:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 0D, TestDateUtil.parseDateTime("2017-11-01 05:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 100D, TestDateUtil.parseDateTime("2017-11-02 00:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 100D, TestDateUtil.parseDateTime("2017-11-03 00:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 200D, TestDateUtil.parseDateTime("2017-11-04 00:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 203D, TestDateUtil.parseDateTime("2017-11-05 00:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 200D, TestDateUtil.parseDateTime("2017-11-06 00:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 200D, TestDateUtil.parseDateTime("2017-11-07 00:00:00"));
		checkStockHistory(history, existingBusinessUnit2, 200D, TestDateUtil.parseDateTime("2017-11-08 00:00:00"));
	}

	private void setupStock() {
		stockBookingTestHelper.createStockMessage(existingItem, stockCenter, existingBusinessUnit, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-01 00:00:01")));
		stockBookingTestHelper.createStockMessage(existingItem, stockCenter, existingBusinessUnit2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-01 05:00:01")));

		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, stockCenter, existingBusinessUnit, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-02 00:00:01"))));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, stockCenter, existingBusinessUnit2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-03 00:00:01"))));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, existingBusinessUnit, existingBusinessUnit2, 3D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-04 00:00:01"))));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, existingBusinessUnit2, existingBusinessUnit, 3D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-05 00:00:01"))));
	}

	private void setupInvalidStock() throws InterruptedException {
		stockBookingTestHelper.createStockMessage(existingItem, existingBusinessUnit, existingBusinessUnit2, 201D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-07 00:00:01")));

		AsyncTestUtil.pollResult(0, () -> stockTransactionBean.getPendingTransactionsCount());

		Stock stock = stockService.findByItemAndBusinessUnit(existingItem.getUuid(), stockCenter.getUuid());
		checkStock(stock, stockCenter, -400D);
		stock = stockService.findByItemAndBusinessUnit(existingItem.getUuid(), existingBusinessUnit.getUuid());
		checkStock(stock, existingBusinessUnit, 200D);
		stock = stockService.findByItemAndBusinessUnit(existingItem.getUuid(), existingBusinessUnit2.getUuid());
		checkStock(stock, existingBusinessUnit2, 200D);

		stockService.deactivate(existingItem.getUuid(), existingBusinessUnit2.getUuid());
		stockBookingTestHelper.createStockMessage(existingItem, existingBusinessUnit, existingBusinessUnit2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-07 00:00:01")));

		stockService.deactivate(existingItem.getUuid(), existingBusinessUnit.getUuid());
		stockBookingTestHelper.createStockMessage(existingItem, existingBusinessUnit, existingBusinessUnit2, 100D,
				dto -> dto.setCreatedAt(TestDateUtil.parseDateTime("2017-11-07 00:00:01")));

	}

	private void checkStock(Stock stock, BusinessUnit businessUnit, Double amount) {
		Assert.assertEquals(existingItem.getUuid(), stock.getItem().getUuid());
		Assert.assertEquals(businessUnit.getUuid(), stock.getBusinessUnit().getUuid());
		Assert.assertEquals(amount, stock.getAmount());
	}

	private void checkStockHistory(List<StockHistory> stockHistory, BusinessUnit businessUnit, Double amount, Date validityDate) {
		boolean found = false;
		for (StockHistory stock : stockHistory) {
			Assert.assertEquals(existingItem.getUuid(), stock.getItem().getUuid());
			Assert.assertEquals(businessUnit.getUuid(), stock.getBusinessUnit().getUuid());
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
