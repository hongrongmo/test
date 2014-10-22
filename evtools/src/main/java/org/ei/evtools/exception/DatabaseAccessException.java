package org.ei.evtools.exception;

public class DatabaseAccessException extends BaseException {

	private static final long serialVersionUID = -7953677453589502742L;

	public DatabaseAccessException(String message, Exception e) {
		super(message, e);
	}

	public DatabaseAccessException(Exception e) {
		super(e);
	}
}
