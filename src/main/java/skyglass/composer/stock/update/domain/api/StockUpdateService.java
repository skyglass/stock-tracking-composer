package skyglass.composer.stock.update.domain.api;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

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

			if (stocksAreDifferent(stockUpdate.getFromUuid(), stockUpdate.getToUuid())) {
				stockUpdateProcessor.updateStock(stockUpdate.getFromUuid(), stockUpdate.getItemUuid(),
						() -> stockUpdateBean.changeStockFrom(stockUpdate));
			}
		} catch (IOException | SQLException ex) {
			throw new ClientException(ex);
		}
	}

	private boolean stocksAreDifferent(String from, String to) {
		return !Objects.equals(from, to);
	}

}
