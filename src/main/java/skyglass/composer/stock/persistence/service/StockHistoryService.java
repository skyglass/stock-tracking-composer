package skyglass.composer.stock.persistence.service;

import java.util.Date;
import java.util.List;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockHistory;

public interface StockHistoryService {

	StockHistory findByUuid(String uuid);

	List<StockHistory> find(Item item, BusinessUnit businessUnit);

	List<StockHistory> findForPeriod(Item item, BusinessUnit businessUnit, Date startDate, Date endDate);

}
