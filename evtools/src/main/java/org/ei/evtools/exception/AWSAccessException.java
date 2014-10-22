package org.ei.evtools.exception;

public class AWSAccessException extends BaseException {

	private static final long serialVersionUID = -8009407300683818781L;

	public AWSAccessException(String message, Exception e) {
		super(message, e);
	}

	public AWSAccessException(Exception e) {
		super(e);
	}
}
