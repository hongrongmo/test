package org.ei.exception;

public class InfrastructureException extends EVBaseException {

	private static final long serialVersionUID = 4221199906744392024L;

	public InfrastructureException(int code, String message, Exception e) {
		super(code, message, e);
	}

	public InfrastructureException(int code, Exception e) {
		super(code, e);
	}

	public InfrastructureException(int code, String message) {
		super(code, message);
	}

}
