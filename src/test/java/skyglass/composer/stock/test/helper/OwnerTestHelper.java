package skyglass.composer.stock.test.helper;

import java.util.function.Consumer;

import skyglass.composer.security.domain.model.Owner;
import skyglass.composer.security.entity.service.OwnerService;

public class OwnerTestHelper {

	private OwnerService ownerApi;

	public static OwnerTestHelper create(OwnerService ownerApi) {
		return new OwnerTestHelper(ownerApi);
	}

	private OwnerTestHelper(OwnerService ownerApi) {
		this.ownerApi = ownerApi;
	}

	public Owner create(String name) {
		return ownerApi.create(createDto(name, null));
	}

	private Owner createDto(String name, Consumer<Owner> consumer) {
		Owner dto = new Owner(null, name);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

}
