package skyglass.composer.stock.entity.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.factory.StockHistoryFactory;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockHistory;
import skyglass.composer.stock.domain.repository.StockHistoryRepository;
import skyglass.composer.stock.entity.model.StockHistoryEntity;

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
	public List<StockHistory> find(Item item, BusinessUnit businessUnit) {
		return stockHistoryFactory.objectList(stockHistoryRepository.find(item, businessUnit));
	}

	@Override
	public List<StockHistory> findForPeriod(Item item, BusinessUnit businessUnit, Date startDate, Date endDate) {
		return stockHistoryFactory.objectList(stockHistoryRepository.findForPeriod(item, businessUnit, startDate, endDate));
	}

}
