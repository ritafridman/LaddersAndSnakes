package smartspace.layout;

public class UserNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6929114331085031181L;
	
	public UserNotFoundException() {
	}

	public UserNotFoundException(String message) {
		super(message);
	}

	public UserNotFoundException(Throwable cause) {
		super(cause);
	}

	public UserNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

}

