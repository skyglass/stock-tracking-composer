package skyglass.composer.stock.persistence.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockHistory;
import skyglass.composer.stock.domain.service.StockHistoryBean;
import skyglass.composer.stock.persistence.entity.StockHistoryEntity;
import skyglass.composer.stock.persistence.repository.StockHistoryRepository;

@Service
@Transactional
class StockHistoryServiceImpl implements StockHistoryService {

	private final StockHistoryRepository stockHistoryRepository;

	private final StockHistoryBean stockHistoryBean;

	@PersistenceContext
	private EntityManager entityManager;

	StockHistoryServiceImpl(StockHistoryBean stockHistoryBean, StockHistoryRepository stockHistoryRepository) {
		this.stockHistoryBean = stockHistoryBean;
		this.stockHistoryRepository = stockHistoryRepository;
	}

	@Override
	public StockHistory findByUuid(String uuid) {
		StockHistoryEntity entity = this.stockHistoryRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return StockHistory.mapEntity(entity);
	}

	@Override
	public List<StockHistory> find(Item item, BusinessUnit businessUnit) {
		return StockHistory.mapEntityList(stockHistoryBean.find(item, businessUnit));
	}

	@Override
	public List<StockHistory> findForPeriod(Item item, BusinessUnit businessUnit, Date startDate, Date endDate) {
		return StockHistory.mapEntityList(stockHistoryBean.findForPeriod(item, businessUnit, startDate, endDate));
	}

}
