package skyglass.composer.stock.entity.service;

import java.util.Date;
import java.util.List;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockHistory;

public interface StockHistoryService {

	StockHistory findByUuid(String uuid);

	List<StockHistory> find(Item item, Context context);

	List<StockHistory> findForPeriod(Item item, Context context, Date startDate, Date endDate);

}
