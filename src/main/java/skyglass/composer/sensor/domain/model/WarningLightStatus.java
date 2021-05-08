package skyglass.composer.sensor.domain.model;

public enum WarningLightStatus {

	Unknown(-1), Off(0), On(1), Toggling(2);

	private int value;

	private WarningLightStatus(int value) {
		this.value = value;
	}

	public int getValue() {
		return value;
	}

	public static WarningLightStatus getWarningLightStatus(int state) {
		for (WarningLightStatus l : WarningLightStatus.values()) {
			if (l.value == state)
				return l;
		}
		return Unknown;
	}
}
