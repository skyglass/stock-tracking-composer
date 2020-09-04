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
import skyglass.composer.stock.domain.StockHistory;
import skyglass.composer.stock.persistence.service.StockHistoryService;

@RestController
@RequestMapping("/stock-history")
class StockHistoryResource {

	private final StockHistoryService stockHistoryService;

	StockHistoryResource(StockHistoryService stockHistoryService) {
		this.stockHistoryService = stockHistoryService;
	}

	@GetMapping("/")
	public Iterable<StockHistory> retrieveAllStockHistories() {
		return stockHistoryService.getAll();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find stock history by uuid", notes = "Also returns a link to retrieve all stock histories with rel - all-stock histories")
	public EntityModel<StockHistory> retrieveStockHistory(@PathVariable String uuid) {
		StockHistory stockHistory = stockHistoryService.getByUuid(uuid);

		if (stockHistory == null)
			throw new NotFoundException("uuid-" + uuid);

		EntityModel<StockHistory> resource = EntityModel.of(stockHistory);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllStockHistories());

		resource.add(linkTo.withRel("all-stock-histories"));

		return resource;
	}

}
