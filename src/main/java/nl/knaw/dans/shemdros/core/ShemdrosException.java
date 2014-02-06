package nl.knaw.dans.shemdros.core;

public class ShemdrosException extends Exception {

	private static final long serialVersionUID = 2777942444494962110L;

	public ShemdrosException() {
		
	}

	public ShemdrosException(String message) {
		super(message);
	}

	public ShemdrosException(Throwable cause) {
		super(cause);
	}

	public ShemdrosException(String message, Throwable cause) {
		super(message, cause);
	}

	public ShemdrosException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
