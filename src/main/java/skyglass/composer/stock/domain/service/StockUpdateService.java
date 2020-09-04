package skyglass.composer.stock.domain.service;

import java.io.IOException;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import skyglass.composer.stock.exceptions.ClientException;

@Service
public class StockUpdateService {

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
			stockUpdateProcessor.updateStock(stockUpdate.getToUuid(), stockUpdate.getItemUuid(),
					() -> stockUpdateBean.changeStockTo(stockUpdate));

			if (stockUpdate.shouldUpdateStock()) {
				stockUpdateProcessor.updateStock(stockUpdate.getFromUuid(), stockUpdate.getItemUuid(),
						() -> stockUpdateBean.changeStockFrom(stockUpdate));
			}
		} catch (IOException | SQLException ex) {
			throw new ClientException(ex);
		}
	}

}
