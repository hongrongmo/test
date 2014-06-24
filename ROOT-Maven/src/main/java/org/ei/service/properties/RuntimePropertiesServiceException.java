package org.ei.service.properties;


public class RuntimePropertiesServiceException extends Exception {

	private static final long serialVersionUID = 6865611199810751763L;

	public RuntimePropertiesServiceException(Exception e) {
		super(e);
	}

	public RuntimePropertiesServiceException(String message) {
		super(message);
	}
	
	public RuntimePropertiesServiceException(String message, Exception e) {
		super(message, e);
	}

}
