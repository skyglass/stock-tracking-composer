package skyglass.composer.stock.update.domain.api;

import java.util.concurrent.ExecutionException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.Stock;
import skyglass.composer.stock.domain.api.BusinessUnitService;
import skyglass.composer.stock.domain.api.ItemService;
import skyglass.composer.stock.domain.api.StockService;
import skyglass.composer.stock.test.config.TestDataConstants;
import skyglass.composer.stock.test.helper.StockBookingTestHelper;

// @ActiveProfiles({ AbstractBaseTest.PROFILE_PSQL })
public class StockUpdateTest extends AbstractAsyncStockUpdateTest {

	@Autowired
	private StockBookingService stockBookingService;

	@Autowired
	private BusinessUnitService businessUnitService;

	@Autowired
	private StockService stockService;

	@Autowired
	private ItemService itemService;

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

		Stock stock = stockService.findByItemAndBusinessUnit(existingItem, stockCenter);
		checkStock(stock, stockCenter, -200D);
		stock = stockService.findByItemAndBusinessUnit(existingItem, existingBusinessUnit);
		checkStock(stock, existingBusinessUnit, 100D);
		stock = stockService.findByItemAndBusinessUnit(existingItem, existingBusinessUnit2);
		checkStock(stock, existingBusinessUnit2, 100D);
	}

	private void setupStock() {
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, stockCenter, existingBusinessUnit, 100D, null));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, stockCenter, existingBusinessUnit2, 100D, null));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, existingBusinessUnit, existingBusinessUnit2, 3D, null));
		dtos.add(stockBookingTestHelper.createStockMessageDto(existingItem, existingBusinessUnit2, existingBusinessUnit, 3D, null));
	}

	private void checkStock(Stock stock, BusinessUnit businessUnit, Double amount) {
		Assert.assertEquals(existingItem.getUuid(), stock.getItem().getUuid());
		Assert.assertEquals(businessUnit.getUuid(), stock.getBusinessUnit().getUuid());
		Assert.assertEquals(amount, stock.getAmount());
	}

}
