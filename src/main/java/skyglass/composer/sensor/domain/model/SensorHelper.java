package skyglass.composer.sensor.domain.model;

import java.util.List;

public class SensorHelper {

	public static Sensor findSensorByConvention(List<Sensor> sensors, String sensorid) {
		for (Sensor sensor : sensors) {
			if (sensorid.equalsIgnoreCase(sensor.getSensorId())) {
				return sensor;
			}
		}
		return null;
	}

}
