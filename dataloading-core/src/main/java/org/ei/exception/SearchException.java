package org.ei.exception;

public class SearchException extends EVBaseException {
	private static final long serialVersionUID = 6662746857764611119L;

	public SearchException(int code, String message) {
		super(code, message);
	}

	public SearchException(int code, String message, Exception e) {
		super(code, message, e);
	}

	public SearchException(int code, Exception e) {
		super(code, e.getMessage(), e);
	}
}
