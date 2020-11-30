package skyglass.composer.query.request;

public enum AggFunc {

	sum("sum"), avg("avg"), count("count"), max("max"), min("min"), none("");

	private final String func;

	private AggFunc(String func) {
		this.func = func;
	}

	public String getFunc() {
		return func;
	}

}
