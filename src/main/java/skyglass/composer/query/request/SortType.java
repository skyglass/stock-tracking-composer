package skyglass.composer.query.request;

public enum SortType {

	desc("DESC"), asc("ASC");

	private final String type;

	private SortType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

}
