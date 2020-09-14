package skyglass.composer.stock.domain.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import skyglass.composer.stock.domain.dto.StockMessageDto;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;
import skyglass.composer.stock.entity.model.StockParameterEntity;
import skyglass.composer.stock.entity.repository.BusinessUnitBean;
import skyglass.composer.stock.entity.repository.ItemBean;
import skyglass.composer.stock.entity.repository.StockMessageBean;
import skyglass.composer.stock.exceptions.ClientException;
import skyglass.composer.utils.date.DateUtil;

@Service
public class StockUpdateService {
	
	@Autowired
	private ItemBean itemBean;

	@Autowired
	private BusinessUnitBean businessUnitBean;
	
	@Autowired
	private StockMessageBean stockMessageBean;

	@Autowired
	private StockUpdateConnector stockUpdateConnector;

	private StockUpdateProcessor stockUpdateProcessor;

	@Autowired
	private DataSource dataSource;

	@Autowired
	private StockUpdateBean stockUpdateBean;

	@PostConstruct
	public void init() throws Exception {
		this.stockUpdateProcessor = new StockUpdateProcessor(dataSource, stockUpdateConnector);
	}

	public void changeStock(final StockUpdate stockUpdate) {
		try {
			stockUpdateProcessor.updateStock(stockUpdate.getItemUuid(), stockUpdate.getToUuid(),
					() -> stockUpdateBean.changeStockTo(stockUpdate));

			if (stockUpdate.shouldUpdateStock()) {
				stockUpdateProcessor.updateStock(stockUpdate.getItemUuid(), stockUpdate.getFromUuid(),
						() -> stockUpdateBean.changeStockFrom(stockUpdate));
			}
		} catch (IOException | SQLException ex) {
			throw new ClientException(ex);
		}
	}
	
	public StockUpdate createFromDto(StockMessageDto dto) {
		ItemEntity item = itemBean.findByUuidSecure(dto.getItemUuid());
		BusinessUnitEntity from = businessUnitBean.findByUuidSecure(dto.getFromUuid());
		BusinessUnitEntity to = businessUnitBean.findByUuidSecure(dto.getToUuid());
		StockMessageEntity stockMessage = new StockMessageEntity(null, item, from, to, dto.getAmount(), 0L,
				dto.getCreatedAt() == null ? DateUtil.now() : dto.getCreatedAt(), dto.getId(),
				dto.getStockParameters().stream().map(sp -> new StockParameterEntity(sp.getUuid(), sp.getName(), sp.getValue())).collect(Collectors.toList()));
		stockMessageBean.create(stockMessage);
		return new StockUpdate(Item.mapEntity(item), BusinessUnit.mapEntity(from), BusinessUnit.mapEntity(to), StockMessage.mapEntity(stockMessage));
	}

}
