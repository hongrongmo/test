package org.ei.biz.access;


/**
 * This interface defines the exception type thrown throughout the user
 * authentication library.
 */
public class AccessException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 4805399266543812161L;

	/**
     * Constructor for AccessException
     */
    public AccessException(String arg0, Throwable t) {
        super(arg0, t);
    }
}
