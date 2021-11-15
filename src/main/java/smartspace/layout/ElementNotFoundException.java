package smartspace.layout;

public class ElementNotFoundException extends RuntimeException {

	private static final long serialVersionUID = -6592726172216899291L;
	
	public ElementNotFoundException() {
	}

	public ElementNotFoundException(String message) {
		super(message);
	}

	public ElementNotFoundException(Throwable cause) {
		super(cause);
	}

	public ElementNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}
}
