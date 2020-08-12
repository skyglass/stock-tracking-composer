package skyglass.composer.stock.domain;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/business-unit")
class BusinessUnitResource {

	private final BusinessUnitService businessUnitService;

	BusinessUnitResource(BusinessUnitService businessUnitService) {
		this.businessUnitService = businessUnitService;
	}

	@GetMapping("/")
	public Iterable<BusinessUnit> retrieveAllBusinessUnits() {
		return businessUnitService.getAll();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find business unit by uuid", notes = "Also returns a link to retrieve all business units with rel - all-business units")
	public EntityModel<BusinessUnit> retrieveBusiinessUnit(@PathVariable String uuid) {
		BusinessUnit businessUnit = businessUnitService.getByUuid(uuid);

		if (businessUnit == null)
			throw new NotFoundException("uuid-" + uuid);

		EntityModel<BusinessUnit> resource = EntityModel.of(businessUnit);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllBusinessUnits());

		resource.add(linkTo.withRel("all-business-units"));

		return resource;
	}

}
