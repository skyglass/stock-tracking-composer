package skyglass.composer.stock.exceptions;

public class InvalidTransactionStateException extends RuntimeException {

	private static final long serialVersionUID = -8939228888639385149L;

	public InvalidTransactionStateException(String message, Throwable e) {
		super(message, e);
	}

}
