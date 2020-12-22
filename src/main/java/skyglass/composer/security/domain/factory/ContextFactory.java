package skyglass.composer.security.domain.factory;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.common.factory.AObjectParamsFactory;
import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.security.entity.model.OwnerEntity;
import skyglass.composer.security.entity.repository.ContextRepository;
import skyglass.composer.utils.AssertUtil;

@Component
public class ContextFactory extends AObjectParamsFactory<Context, ContextEntity> {

	@Autowired
	private OwnerFactory ownerFactory;

	@Autowired
	private ContextRepository contextRepository;

	@Override
	public Context createObject(ContextEntity entity) {
		return entity == null ? null
				: new Context(entity.getUuid(), entity.getName(),
						ownerFactory.object(entity.getOwner()), object(entity.getParent()));
	}

	@Override
	public ContextEntity createEntityFromParams(Context object, Object... params) {
		ContextEntity parentEntity = getParent(params);
		checkParentNotChanged(object, parentEntity);
		OwnerEntity ownerEntity = parentEntity != null ? parentEntity.getOwner()
				: ownerFactory.entity(object.getOwner());
		int level = parentEntity == null ? 0 : parentEntity.getLevel() + 1;
		return object == null ? null
				: new ContextEntity(object.getUuid(), object.getName(),
						ownerEntity, parentEntity, level);
	}

	private ContextEntity getParent(Object... params) {
		String parentUuid = null;
		for (Object param : params) {
			parentUuid = (String) param;
			break;
		}
		if (parentUuid == null) {
			return null;
		}
		return contextRepository.findByUuidSecure(parentUuid);
	}

	private void checkParentNotChanged(Context object, ContextEntity parent) {
		if (StringUtils.isNotBlank(object.getUuid())) {
			ContextEntity dbEntity = contextRepository.findByUuidSecure(object.getUuid());
			AssertUtil.isTrue(String.format(
					"Context Update Error: Context parent can not be changed (name = %s)", dbEntity.getName()),
					Objects.equals(dbEntity.getParent(), parent));
		}
	}

}
