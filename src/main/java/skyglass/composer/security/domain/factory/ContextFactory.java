package skyglass.composer.security.domain.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.model.ContextEntity;
import skyglass.composer.stock.AObjectFactory;

@Component
public class ContextFactory extends AObjectFactory<Context, ContextEntity> {

	@Autowired
	private OwnerFactory ownerFactory;

	@Override
	public Context createObject(ContextEntity entity) {
		return entity == null ? null
				: new Context(entity.getUuid(), entity.getName(),
						ownerFactory.object(entity.getOwner()), object(entity.getParent()));
	}

	@Override
	public ContextEntity createEntity(Context object) {
		return object == null ? null
				: new ContextEntity(object.getUuid(), object.getName(),
						ownerFactory.entity(object.getOwner()), entity(object.getParent()));
	}

}
