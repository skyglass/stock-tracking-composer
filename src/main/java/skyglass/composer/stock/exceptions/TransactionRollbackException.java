package skyglass.composer.stock.exceptions;

public class TransactionRollbackException extends  InternalException {

	private static final long serialVersionUID = 6398751818260410963L;
	
	public TransactionRollbackException(String message) {
		super(message);
	}

}
