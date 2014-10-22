package org.ei.evtools.exception;

public class DatabaseConfigException extends BaseException {

	private static final long serialVersionUID = -7953677453589502742L;

	public DatabaseConfigException(String message, Exception e) {
		super(message, e);
	}

	public DatabaseConfigException(Exception e) {
		super(e);
	}
}
