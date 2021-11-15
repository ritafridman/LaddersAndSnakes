package smartspace.layout;

public class BadImportRequestException extends RuntimeException {
	private static final long serialVersionUID = 2930562196455266604L;

	public BadImportRequestException() {
	}

	public BadImportRequestException(String message) {
		super(message);
	}

	public BadImportRequestException(Throwable cause) {
		super(cause);
	}

	public BadImportRequestException(String message, Throwable cause) {
		super(message, cause);
	}
}
