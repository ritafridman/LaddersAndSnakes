package smartspace.layout;

public class NotExistPluginNameException extends Exception {

	private static final long serialVersionUID = 1318369201296972484L;

	public NotExistPluginNameException() {
		super();
	}

	public NotExistPluginNameException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public NotExistPluginNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotExistPluginNameException(String message) {
		super(message);
	}

	public NotExistPluginNameException(Throwable cause) {
		super(cause);
	}

}
