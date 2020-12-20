package skyglass.composer.security.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import skyglass.composer.security.domain.model.Context;
import skyglass.composer.security.entity.service.ContextService;
import skyglass.composer.stock.domain.model.NotFoundException;

@RestController
@RequestMapping("/context")
class ContextResource {

	private final ContextService contextService;

	ContextResource(ContextService contextService) {
		this.contextService = contextService;
	}

	@GetMapping("/")
	public List<Context> retrieveAllContexts() {
		return contextService.findAll();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find business unit by uuid", notes = "Also returns a link to retrieve all business units with rel - all-business units")
	public EntityModel<Context> retrieveContext(@PathVariable String uuid) {
		Context context = contextService.getByUuid(uuid);

		if (context == null)
			throw new NotFoundException("uuid-" + uuid);

		EntityModel<Context> resource = EntityModel.of(context);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllContexts());

		resource.add(linkTo.withRel("all-business-units"));

		return resource;
	}

}
