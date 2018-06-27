package org.ei.exception;


public class SessionException extends EVBaseException {

	/**
	 * Default serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	public SessionException(int code, Exception e) {
		super(code, e);
	}

	public SessionException(int code, String message, Exception e) {
		super(code, message, e);
	}

	public SessionException(int code, String message) {
		super(code, message, null);
	}

}
