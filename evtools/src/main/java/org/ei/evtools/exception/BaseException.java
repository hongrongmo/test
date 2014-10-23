package org.ei.evtools.exception;


public class BaseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5516452598755181406L;
	//
	// Unmapped error codes 
	//
	public static final int UNKNOWN = -1;
	public static final int ERROR_XML_CONVERSION_FAILED = -2;
	public static final int CANNOT_PROCESS_XML_ERROR = -3;


	int code = UNKNOWN;
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Exception getOrigin() {
		return origin;
	}

	public void setOrigin(Exception origin) {
		this.origin = origin;
	}

	String message = "An unknown error has occurred";
	Exception origin;

	public BaseException(Exception e) {
		super(e);
		this.origin = e;
		this.message = e.getMessage();
	}
	
	public BaseException(String message, Exception e) {
		super(message, e);
		this.origin = e;
		this.message = message;
	}

	public BaseException(int code, String message) {
		super(message);
		this.code = code;
		this.message = message;
	}

	public BaseException(String message) {
		super(message);
		this.message = message;
	}

	public BaseException(int code, String message, Exception e) {
		super(message, e);
		this.code = code;
		this.origin = e;
		this.message = message;
	}

	public BaseException(int code, Exception e) {
		super(e);
		this.code = code;
		this.origin = e;
		this.message = (e == null ? "Invalid constructor - no exception passed!" : e.getMessage());
	}
}
