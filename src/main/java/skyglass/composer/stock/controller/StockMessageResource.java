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
import skyglass.composer.stock.domain.model.NotFoundException;
import skyglass.composer.stock.domain.model.StockMessage;
import skyglass.composer.stock.entity.service.StockMessageService;

@RestController
@RequestMapping("/stock-message")
class StockMessageResource {

	private final StockMessageService stockMessageService;

	StockMessageResource(StockMessageService stockMessageService) {
		this.stockMessageService = stockMessageService;
	}

	@GetMapping("/")
	public Iterable<StockMessage> retrieveAllStockMessages() {
		return stockMessageService.getAll();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find stock message by uuid", notes = "Also returns a link to retrieve all stock messages with rel - all-stock messages")
	public EntityModel<StockMessage> retrieveStockMessage(@PathVariable String uuid) {
		StockMessage stockMessage = stockMessageService.getByUuid(uuid);

		if (stockMessage == null)
			throw new NotFoundException("uuid-" + uuid);

		EntityModel<StockMessage> resource = EntityModel.of(stockMessage);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllStockMessages());

		resource.add(linkTo.withRel("all-stock-messages"));

		return resource;
	}

}
