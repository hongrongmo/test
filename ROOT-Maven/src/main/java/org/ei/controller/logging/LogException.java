package org.ei.controller.logging;

/**
 * This class is meant to be used in conjunction with the org.ei.controller.logging.Logger
 * class
 * 
 */
public class LogException extends Exception {

	private Exception e;
	private String message;
	
	public LogException(String message) {
		super(message);
	}

	public LogException(Exception e) {
		super(e);
	}

	
}
