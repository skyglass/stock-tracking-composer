package skyglass.composer.stock.domain.repository;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.dto.StockMessageDto;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.model.BusinessUnitEntity;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.ItemEntity;
import skyglass.composer.stock.entity.model.StockMessageEntity;

@Repository
@Transactional
public class StockMessageBean extends AEntityBean<StockMessageEntity> {

	@Autowired
	private ItemBean itemBean;

	@Autowired
	private BusinessUnitBean businessUnitBean;

	@Autowired
	private StockTransactionBean stockTransactionBean;

	public StockMessageEntity findByMessageId(String messageId) {
		if (StringUtils.isBlank(messageId)) {
			return null;
		}

		TypedQuery<StockMessageEntity> query = entityBeanUtil.createQuery("SELECT sm FROM StockMessage sm WHERE sm.messageId = :messageId", StockMessageEntity.class);
		query.setParameter("messageId", messageId);
		query.setMaxResults(1);
		return EntityUtil.getSingleResultSafely(query);
	}

	public StockMessage createFromDto(StockMessageDto dto) {
		ItemEntity item = itemBean.findByUuidSecure(dto.getItemUuid());
		BusinessUnitEntity from = businessUnitBean.findByUuidSecure(dto.getFromUuid());
		BusinessUnitEntity to = businessUnitBean.findByUuidSecure(dto.getToUuid());
		StockMessageEntity stockMessage = create(StockMessage.createEntity(dto, item, from, to));
		stockTransactionBean.create(stockMessage);
		return StockMessage.mapEntity(stockMessage);
	}

}
