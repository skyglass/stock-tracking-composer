package skyglass.composer.stock;

public class LocationNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1104211497123502790L;

	public LocationNotFoundException(String exception) {
		super(exception);
	}

}
