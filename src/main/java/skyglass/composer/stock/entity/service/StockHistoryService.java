package skyglass.composer.stock.entity.service;

import java.util.Date;
import java.util.List;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockHistory;

public interface StockHistoryService {

	StockHistory findByUuid(String uuid);

	List<StockHistory> find(Item item, BusinessUnit businessUnit);

	List<StockHistory> findForPeriod(Item item, BusinessUnit businessUnit, Date startDate, Date endDate);

}
