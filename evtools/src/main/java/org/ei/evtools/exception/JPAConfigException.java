package org.ei.evtools.exception;

public class JPAConfigException extends BaseException {

	private static final long serialVersionUID = 1L;

	public JPAConfigException(String message, Exception e) {
		super(message, e);
	}

	public JPAConfigException(Exception e) {
		super(e);
	}
}
