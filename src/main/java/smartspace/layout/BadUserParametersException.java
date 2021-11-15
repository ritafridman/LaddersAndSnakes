package smartspace.layout;

public class BadUserParametersException extends RuntimeException{

	private static final long serialVersionUID = 2953762364152259744L;
	
	public BadUserParametersException() {
	}

	public BadUserParametersException(String message) {
		super(message);
	}

	public BadUserParametersException(Throwable cause) {
		super(cause);
	}

	public BadUserParametersException(String message, Throwable cause) {
		super(message, cause);
	}

}
