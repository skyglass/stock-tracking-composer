package skyglass.composer.stock;

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
@RequestMapping("/locations")
class LocationResource {

	private final LocationService locationService;

	LocationResource(LocationService locationService) {
		this.locationService = locationService;
	}

	@GetMapping("/")
	public Iterable<Location> retrieveAllLocations() {
		return locationService.getLocations();
	}

	@GetMapping("/{uuid}")
	@ApiOperation(value = "Find location by uuid", notes = "Also returns a link to retrieve all locations with rel - all-locations")
	public EntityModel<Location> retrieveLocation(@PathVariable String uuid) {
		Location location = locationService.getLocationByUuid(uuid);

		if (location == null)
			throw new LocationNotFoundException("uuid-" + uuid);

		EntityModel<Location> resource = EntityModel.of(location);

		WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllLocations());

		resource.add(linkTo.withRel("all-locations"));

		return resource;
	}

	@GetMapping("find-closest/{latitude}/{longitude}")
	@ApiOperation(value = "Find 5 closest locations by user's current latitude and longitude", notes = "Use https://www.where-am-i.net/ to get your current coordinates")
	public Iterable<Location> findClosest(@PathVariable Double latitude, @PathVariable Double longitude) {
		return locationService.findClosest(latitude, longitude);
	}

}
