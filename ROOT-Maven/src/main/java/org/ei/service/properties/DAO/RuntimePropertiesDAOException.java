package org.ei.service.properties.DAO;


public class RuntimePropertiesDAOException extends Exception {

	private static final long serialVersionUID = 755118309809475194L;

	public RuntimePropertiesDAOException(Exception e) {
		super(e);
	}

	public RuntimePropertiesDAOException(String message) {
		super(message);
	}
	
	public RuntimePropertiesDAOException(String message, Exception e) {
		super(message, e);
	}

}
