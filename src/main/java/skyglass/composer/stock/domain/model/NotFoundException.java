package skyglass.composer.stock.domain.model;

public class NotFoundException extends RuntimeException {

	private static final long serialVersionUID = -1104211497123502790L;

	public NotFoundException(String exception) {
		super(exception);
	}

}
