package skyglass.composer.stock.test.helper;

import java.util.function.Consumer;

import skyglass.composer.stock.domain.model.BusinessOwner;
import skyglass.composer.stock.entity.service.BusinessOwnerService;

public class BusinessOwnerTestHelper {

	private BusinessOwnerService businessOwnerApi;

	public static BusinessOwnerTestHelper create(BusinessOwnerService businessOwnerApi) {
		return new BusinessOwnerTestHelper(businessOwnerApi);
	}

	private BusinessOwnerTestHelper(BusinessOwnerService businessOwnerApi) {
		this.businessOwnerApi = businessOwnerApi;
	}

	public BusinessOwner create(String name) {
		return businessOwnerApi.create(createDto(name, null));
	}

	private BusinessOwner createDto(String name, Consumer<BusinessOwner> consumer) {
		BusinessOwner dto = new BusinessOwner(null, name);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

}
