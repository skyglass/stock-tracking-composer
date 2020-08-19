package skyglass.composer.db.exceptions;

public class LockedException extends Exception {

	private static final long serialVersionUID = -5794635212239071444L;

	public LockedException() {
	}

	public LockedException(Throwable cause) {
		super(cause);
	}
}
