package smartspace.layout;

public class DoCheckOutOnCheckOutException extends Exception{


	private static final long serialVersionUID = -8946127589828848002L;

	public DoCheckOutOnCheckOutException() {
		super();
	}

	public DoCheckOutOnCheckOutException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DoCheckOutOnCheckOutException(String message, Throwable cause) {
		super(message, cause);
	}

	public DoCheckOutOnCheckOutException(String message) {
		super(message);
	}

	public DoCheckOutOnCheckOutException(Throwable cause) {
		super(cause);
	}

}
