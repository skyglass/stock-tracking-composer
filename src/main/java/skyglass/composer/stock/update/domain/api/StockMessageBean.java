package skyglass.composer.stock.update.domain.api;

import java.util.stream.Collectors;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityBean;
import skyglass.composer.stock.domain.BusinessUnit;
import skyglass.composer.stock.domain.Item;
import skyglass.composer.stock.domain.StockMessage;
import skyglass.composer.stock.dto.StockMessageDto;
import skyglass.composer.stock.persistence.entity.BusinessUnitEntity;
import skyglass.composer.stock.persistence.entity.EntityUtil;
import skyglass.composer.stock.persistence.entity.ItemEntity;
import skyglass.composer.stock.persistence.entity.StockMessageEntity;
import skyglass.composer.stock.persistence.entity.StockParameterEntity;

@Repository
@Transactional
public class StockMessageBean extends AEntityBean<StockMessageEntity> {

	@Autowired
	private ItemBean itemBean;

	@Autowired
	private BusinessUnitBean businessUnitBean;

	public StockMessageEntity findByMessageId(String messageId) {
		if (StringUtils.isBlank(messageId)) {
			return null;
		}

		TypedQuery<StockMessageEntity> query = entityBeanUtil.createQuery("SELECT sm FROM StockMessage sm WHERE sm.messageId = :messageId", StockMessageEntity.class);
		query.setParameter("messageId", messageId);
		query.setMaxResults(1);
		return EntityUtil.getSingleResultSafely(query);
	}

	public StockUpdate createFromDto(StockMessageDto dto) {
		ItemEntity item = itemBean.findByUuidSecure(dto.getItemUuid());
		BusinessUnitEntity from = businessUnitBean.findByUuidSecure(dto.getFromUuid());
		BusinessUnitEntity to = businessUnitBean.findByUuidSecure(dto.getToUuid());
		StockMessageEntity stockMessage = new StockMessageEntity(null, item, from, to, dto.getAmount(), null, null, dto.getId(),
				dto.getStockParameters().stream().map(sp -> new StockParameterEntity(sp.getUuid(), sp.getName(), sp.getValue())).collect(Collectors.toList()));
		create(stockMessage);
		return new StockUpdate(Item.mapEntity(item), BusinessUnit.mapEntity(from), BusinessUnit.mapEntity(to), StockMessage.mapEntity(stockMessage));
	}

}
