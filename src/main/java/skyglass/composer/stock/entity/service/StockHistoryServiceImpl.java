package skyglass.composer.stock.entity.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.stock.domain.factory.StockHistoryFactory;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockHistory;
import skyglass.composer.stock.entity.model.StockHistoryEntity;
import skyglass.composer.stock.entity.repository.StockHistoryRepository;

@Service
@Transactional
class StockHistoryServiceImpl implements StockHistoryService {

	@Autowired
	private StockHistoryRepository stockHistoryRepository;

	@Autowired
	private StockHistoryFactory stockHistoryFactory;

	@Override
	public StockHistory findByUuid(String uuid) {
		StockHistoryEntity entity = this.stockHistoryRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return stockHistoryFactory.object(entity);
	}

	@Override
	public List<StockHistory> find(Item item, Context context) {
		return stockHistoryFactory.objectList(stockHistoryRepository.find(item, context));
	}

	@Override
	public List<StockHistory> findForPeriod(Item item, Context context, Date startDate, Date endDate) {
		return stockHistoryFactory.objectList(stockHistoryRepository.findForPeriod(item, context, startDate, endDate));
	}

}
