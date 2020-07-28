package skyglass.composer.stock.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import skyglass.composer.stock.Location;
import skyglass.composer.stock.LocationService;
import skyglass.composer.stock.LocationStore;

@Component
class JpaLocationService implements LocationService {

	private final LocationRepository locationRepository;

	@PersistenceContext
	private EntityManager entityManager;

	JpaLocationService(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	@PostConstruct
	public void init() throws IOException {
		importLocations();
	}

	@Override
	public Iterable<Location> getLocations() {
		return StreamSupport.stream(locationRepository.findAll().spliterator(), false)
				.map(this::mapEntity)
				.collect(Collectors.toList());
	}

	@Override
	public Location getLocationByUuid(String uuid) {
		LocationEntity entity = this.locationRepository.findByUuid(uuid);
		if (entity == null) {
			return null;
		}

		return mapEntity(entity);
	}

	Location mapEntity(LocationEntity entity) {
		return new Location(entity.getUuid(), entity.getName());

	}

	LocationEntity map(Location entity) {
		return new LocationEntity(entity.getUuid(), entity.getCity(), entity.getPostalCode(), entity.getStreet(), entity.getStreet2(),
				entity.getStreet3(), entity.getAddressName(), entity.getLatitude(), entity.getLongitude(), entity.getTodayOpen(),
				entity.getTodayClose(), entity.getComplexNumber(), entity.isShowWarningMessage(), entity.getLocationType(),
				entity.isCollectionPoint(), entity.getSapStoreID());

	}

	private void importLocations() throws IOException {
		// read json and write to db
		ObjectMapper mapper = new ObjectMapper();
		InputStream inputStream = TypeReference.class.getResourceAsStream("/locations.json");
		try {
			List<LocationEntity> locations = StreamSupport.stream(
					mapper.readValue(inputStream, LocationStore.class).getStores().spliterator(), false)
					.map(this::map)
					.collect(Collectors.toList());
			locationRepository.saveAll(locations);
			System.out.println("Locations Saved!");
		} catch (IOException e) {
			System.out.println("Unable to save locations: " + e.getMessage());
		}

	}

	@Override
	public Iterable<Location> findClosest(double latitude, double longitude) {
		String queryStr = "SELECT l FROM LocationEntity l "
				+ "ORDER BY ABS(l.latitude - :latitude) + ABS(l.longitude - :longitude)";
		TypedQuery<LocationEntity> typedQuery = entityManager.createQuery(queryStr, LocationEntity.class)
				.setParameter("latitude", latitude)
				.setParameter("longitude", longitude);
		typedQuery.setMaxResults(5);

		return StreamSupport.stream(typedQuery.getResultList().spliterator(), false)
				.map(this::mapEntity)
				.collect(Collectors.toList());

	}

}
