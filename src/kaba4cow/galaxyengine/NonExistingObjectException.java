package kaba4cow.galaxyengine;

public class NonExistingObjectException extends Exception {

	private static final long serialVersionUID = 5312850130398480551L;

	public NonExistingObjectException() {
		super();
	}

	public NonExistingObjectException(String message) {
		super(message);
	}

	public NonExistingObjectException(Throwable throwable) {
		super(throwable);
	}

	public NonExistingObjectException(String message, Throwable throwable) {
		super(message, throwable);
	}

	public NonExistingObjectException(String message, Throwable throwable,
			boolean arg2, boolean arg3) {
		super(message, throwable, arg2, arg3);
	}

}
