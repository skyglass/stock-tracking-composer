package skyglass.composer.stock.exceptions;

public abstract class InternalException extends Exception {

	private static final long serialVersionUID = -41893476651120777L;
	
	private String message;
	
	public InternalException(String message) {
		this.message = message;
	}	
	
	public String getMessage() {
		return message;
	}

}
