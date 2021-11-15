package smartspace.layout;

public class BadElementParametersException extends RuntimeException {
	private static final long serialVersionUID = -5956928661134184555L;
	
	public BadElementParametersException() {
	}

	public BadElementParametersException(String message) {
		super(message);
	}

	public BadElementParametersException(Throwable cause) {
		super(cause);
	}

	public BadElementParametersException(String message, Throwable cause) {
		super(message, cause);
	}
}
