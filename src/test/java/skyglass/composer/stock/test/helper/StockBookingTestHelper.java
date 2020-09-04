package skyglass.composer.stock.test.helper;

import java.util.function.Consumer;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockMessage;
import skyglass.composer.stock.domain.service.StockBookingService;
import skyglass.composer.stock.dto.StockMessageDto;

public class StockBookingTestHelper {

	private StockBookingService stockBookingApi;

	public static StockBookingTestHelper create(StockBookingService stockBookingApi) {
		return new StockBookingTestHelper(stockBookingApi);
	}

	private StockBookingTestHelper(StockBookingService stockBookingApi) {
		this.stockBookingApi = stockBookingApi;
	}

	public StockMessage createStockMessage(Item item, BusinessUnit from, BusinessUnit to, Double amount) {
		return createStockMessage(item, from, to, amount);
	}

	public StockMessage createStockMessage(Item item, BusinessUnit from, BusinessUnit to, Double amount, Consumer<StockMessageDto> consumer) {
		StockMessageDto dto = createStockMessageDto(item, from, to, amount, consumer);
		return createStockMessageFromDto(dto);
	}

	private StockMessage createStockMessageFromDto(StockMessageDto dto) {
		return stockBookingApi.createStockMessage(dto);
	}

	public StockMessageDto createStockMessageDto(Item item, BusinessUnit from, BusinessUnit to, Double amount, Consumer<StockMessageDto> consumer) {
		StockMessageDto dto = new StockMessageDto();
		dto.setItemUuid(item.getUuid());
		dto.setFromUuid(from.getUuid());
		dto.setToUuid(to.getUuid());
		dto.setAmount(amount);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

}
