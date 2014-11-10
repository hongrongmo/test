package org.ei.books;


public class BookException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 130296571621996864L;

	public BookException() {
		super();
	}

	public BookException(String message, Throwable cause) {
		super(message, cause);
	}

	public BookException(String message) {
		super(message);
	}

	public BookException(Throwable cause) {
		super(cause);
	}

	public BookException(Exception e) {
		super(e);
	}

}