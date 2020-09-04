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
import skyglass.composer.stock.domain.Stock;
import skyglass.composer.stock.persistence.service.StockService;

@RestController
@RequestMapping("/stock")
class StockResource {

	private final StockService stockService;

	StockResource(StockService stockService) {
		this.stockService = stockService;
	}

	@GetMapping("/")
	public Iterable<Stock> retrieveAllStocks() {
		return stockService.getAll();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find stock by uuid", notes = "Also returns a link to retrieve all stocks with rel - all-stocks")
	public EntityModel<Stock> retrieveStock(@PathVariable String uuid) {
		Stock stock = stockService.getByUuid(uuid);

		if (stock == null)
			throw new NotFoundException("uuid-" + uuid);

		EntityModel<Stock> resource = EntityModel.of(stock);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllStocks());

		resource.add(linkTo.withRel("all-stocks"));

		return resource;
	}

}
