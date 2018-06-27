package org.ei.exception;

public class ServiceException extends EVBaseException {

	public ServiceException(int code, Exception e) {
		super(code, e);
	}

	public ServiceException(int code, String message, Exception e) {
		super(code, message, e);
	}

	public ServiceException(int code, String message) {
		super(code, message, null);
	}

}
