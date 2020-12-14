package skyglass.composer.stock.test.helper;

import java.util.function.Consumer;

import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.service.ContextService;

public class ContextTestHelper {

	private ContextService contextApi;

	public static ContextTestHelper create(ContextService contextApi) {
		return new ContextTestHelper(contextApi);
	}

	private ContextTestHelper(ContextService contextApi) {
		this.contextApi = contextApi;
	}

	public Context create(String name, Owner owner) {
		return create(name, owner, null);
	}

	public Context create(String name, Owner owner, Context parent) {
		return contextApi.create(createDto(name, owner, parent, null));
	}

	private Context createDto(String name, Owner owner, Context parent, Consumer<Context> consumer) {
		Context dto = new Context(null, name, owner, parent);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

}
