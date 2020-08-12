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
@RequestMapping("/item")
class ItemResource {

	private final ItemService itemService;

	ItemResource(ItemService itemService) {
		this.itemService = itemService;
	}

	@GetMapping("/")
	public Iterable<Item> retrieveAllItems() {
		return itemService.getAll();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find item by uuid", notes = "Also returns a link to retrieve all items with rel - all-items")
	public EntityModel<Item> retrieveItem(@PathVariable String uuid) {
		Item item = itemService.getByUuid(uuid);

		if (item == null)
			throw new NotFoundException("uuid-" + uuid);

		EntityModel<Item> resource = EntityModel.of(item);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllItems());

		resource.add(linkTo.withRel("all-items"));

		return resource;
	}

}
