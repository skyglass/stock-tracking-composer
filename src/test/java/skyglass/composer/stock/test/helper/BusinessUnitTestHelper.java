package skyglass.composer.stock.test.helper;

import java.util.function.Consumer;

import skyglass.composer.stock.domain.model.BusinessOwner;
import skyglass.composer.stock.domain.model.BusinessUnit;
import skyglass.composer.stock.entity.service.BusinessUnitService;

public class BusinessUnitTestHelper {

	private BusinessUnitService businessUnitApi;

	public static BusinessUnitTestHelper create(BusinessUnitService businessUnitApi) {
		return new BusinessUnitTestHelper(businessUnitApi);
	}

	private BusinessUnitTestHelper(BusinessUnitService businessUnitApi) {
		this.businessUnitApi = businessUnitApi;
	}

	public BusinessUnit create(String name, BusinessOwner owner) {
		return create(name, owner, null);
	}

	public BusinessUnit create(String name, BusinessOwner owner, BusinessUnit parent) {
		return businessUnitApi.create(createDto(name, owner, parent, null));
	}

	private BusinessUnit createDto(String name, BusinessOwner owner, BusinessUnit parent, Consumer<BusinessUnit> consumer) {
		BusinessUnit dto = new BusinessUnit(null, name, owner, parent);
		if (consumer != null) {
			consumer.accept(dto);
		}
		return dto;
	}

}
