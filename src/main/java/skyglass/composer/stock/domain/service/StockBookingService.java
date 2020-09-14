package skyglass.composer.stock.domain.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import skyglass.composer.stock.domain.dto.StockMessageDto;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.repository.StockMessageBean;
import skyglass.composer.stock.entity.service.StockMessageService;
import skyglass.composer.stock.exceptions.NotNullableNorEmptyException;

@Service
public class StockBookingService {

	@Autowired
	private StockMessageBean stockMessageBean;

	@Autowired
	private StockUpdateService stockUpdateService;

	@Autowired
	private StockMessageService stockMessageService;

	public StockMessage createStockMessage(StockMessageDto stockMessageDto) {
		if (StringUtils.isBlank(stockMessageDto.getItemUuid())) {
			throw new NotNullableNorEmptyException("Item UUID");
		}
		if (StringUtils.isBlank(stockMessageDto.getFromUuid())) {
			throw new NotNullableNorEmptyException("From UUID");
		}
		if (StringUtils.isBlank(stockMessageDto.getToUuid())) {
			throw new NotNullableNorEmptyException("To UUID");
		}
		if (stockMessageDto.getAmount() == null) {
			throw new NotNullableNorEmptyException("Amount");
		}
		if (StringUtils.isNotBlank(stockMessageDto.getId())) {

			String messageId = createMessageId(stockMessageDto.getId(), stockMessageDto.getToUuid());

			StockMessageEntity stockMessage = stockMessageBean.findByMessageId(messageId);

			if (stockMessage != null) {
				return StockMessage.mapEntity(stockMessage);
			}

			stockMessageDto.setId(messageId);
		}
		StockUpdate result = stockUpdateService.createFromDto(stockMessageDto);

		//stock update must only happen if creation of stock message is successful
		stockUpdateService.changeStock(result);
		StockMessage stockMessage = stockMessageService.getByUuid(result.getStockMessageUuid());
		return stockMessage;
	}

	private static String createMessageId(String id, String toUuid) {
		return id.concat("_").concat(toUuid);
	}

}
