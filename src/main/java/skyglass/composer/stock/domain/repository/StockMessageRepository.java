package skyglass.composer.stock.domain.repository;

import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import skyglass.composer.stock.AEntityRepository;
import skyglass.composer.stock.entity.model.EntityUtil;
import skyglass.composer.stock.entity.model.StockMessageEntity;

@Repository
@Transactional
public class StockMessageRepository extends AEntityRepository<StockMessageEntity> {

	public StockMessageEntity findByMessageId(String messageId) {
		if (StringUtils.isBlank(messageId)) {
			return null;
		}

		TypedQuery<StockMessageEntity> query = entityBeanUtil.createQuery("SELECT sm FROM StockMessage sm WHERE sm.messageId = :messageId", StockMessageEntity.class);
		query.setParameter("messageId", messageId);
		query.setMaxResults(1);
		return EntityUtil.getSingleResultSafely(query);
	}

}
