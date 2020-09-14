package skyglass.composer.stock.entity.service;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.domain.model.Item;
import skyglass.composer.stock.domain.model.StockHistory;
import skyglass.composer.stock.entity.model.StockHistoryEntity;
import skyglass.composer.stock.entity.repository.StockHistoryBean;

@Service
@Transactional
class StockHistoryServiceImpl implements StockHistoryService {

	private final StockHistoryBean stockHistoryBean;

	@PersistenceContext
	private EntityManager entityManager;

	StockHistoryServiceImpl(StockHistoryBean stockHistoryBean) {
		this.stockHistoryBean = stockHistoryBean;
	}

	@Override
	public StockHistory findByUuid(String uuid) {
		StockHistoryEntity entity = this.stockHistoryBean.findByUuid(uuid);
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
