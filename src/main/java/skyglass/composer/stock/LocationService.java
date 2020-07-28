package skyglass.composer.stock;

public interface LocationService {

	Iterable<Location> getLocations();

	Location getLocationByUuid(String uuid);

	Iterable<Location> findClosest(double latitude, double longitude);

}
