package org.ei.evtools.exception;

/**
 * @author kamaramx
 * @version 1.0
 * 
 */
public class CSServiceException extends BaseException {


	private static final long serialVersionUID = 3667423774765352334L;

	public CSServiceException(String message, Exception e) {
		super(message, e);
	}

	public CSServiceException(Exception e) {
		super(e);
	}

}
