package skyglass.composer.stock.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import skyglass.composer.stock.domain.NotFoundException;
import skyglass.composer.stock.domain.StockMovement;
import skyglass.composer.stock.domain.api.StockMovementService;

@RestController
@RequestMapping("/stock-movement")
class StockMovementResource {

	private final StockMovementService stockMovementService;

	StockMovementResource(StockMovementService stockMovementService) {
		this.stockMovementService = stockMovementService;
	}

	@GetMapping("/")
	public Iterable<StockMovement> retrieveAllStockMovements() {
		return stockMovementService.getAll();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find stock movement by uuid", notes = "Also returns a link to retrieve all stock movements with rel - all-stock movements")
	public EntityModel<StockMovement> retrieveStockMovement(@PathVariable String uuid) {
		StockMovement stockMovement = stockMovementService.getByUuid(uuid);

		if (stockMovement == null)
			throw new NotFoundException("uuid-" + uuid);

		EntityModel<StockMovement> resource = EntityModel.of(stockMovement);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllStockMovements());

		resource.add(linkTo.withRel("all-stock-movements"));

		return resource;
	}

}
