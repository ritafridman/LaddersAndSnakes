package smartspace.layout;

import java.util.Arrays;

public class UnAuthorizedActionOnElementException extends Exception {
	private static final long serialVersionUID = 3889026980924103963L;

	public UnAuthorizedActionOnElementException() {
		super();
	
	}

	public UnAuthorizedActionOnElementException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	
	}

	public UnAuthorizedActionOnElementException(String message, Throwable cause) {
		super(message, cause);
		
	}

	public UnAuthorizedActionOnElementException(String message) {
		super(message);
	
	}

	public UnAuthorizedActionOnElementException(Throwable cause) {
		super(cause);
		
	}

	@Override
	public String toString() {
		return "UnAuthorizedActionOnElementException [getMessage()=" + getMessage();
	}




}
