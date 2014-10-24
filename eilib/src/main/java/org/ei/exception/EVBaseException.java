package org.ei.exception;


public abstract class EVBaseException extends Exception implements XMLBasedException {

	//
	// Unmapped error codes 
	//
	public static final int UNKNOWN = -1;
	public static final int ERROR_XML_CONVERSION_FAILED = -2;
	public static final int CANNOT_PROCESS_XML_ERROR = -3;

	private static final long serialVersionUID = 2854009812925890208L;

	int code = UNKNOWN;
	String message = "An unknown error has occurred";
	Exception origin;

	public EVBaseException(Exception e) {
		super(e);
		this.origin = e;
		this.message = e.getMessage();
	}
	
	public EVBaseException(String message, Exception e) {
		super(message, e);
		this.origin = e;
		this.message = message;
	}

	public EVBaseException(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public EVBaseException(String message) {
		super(message);
		this.message = message;
	}

	public EVBaseException(int code, String message, Exception e) {
		super(message, e);
		this.code = code;
		this.origin = e;
		this.message = message;
	}

	public EVBaseException(int code, Exception e) {
		super(e);
		this.code = code;
		this.origin = e;
		this.message = (e == null ? "Invalid constructor - no exception passed!" : e.getMessage());
	}

	@Override
	public int getErrorCode() {
		return this.code;
	}

	@Override
	public Exception getOriginatedException() {
		return this.origin;
	}

	@Override
	public Exception getBaseException() {
		return this;
	}

	@Override
	public String getCustomerMessage() {
		return "Sorry, a system error has occurred, and your request cannot be completed.";
	}

	public String toXML() {
		return ExceptionWriter.toXml(this);
	}
}
