package org.ei.exception;

/**
 * This interface is meant to be used with Exceptions that need to be converted to/from XML.  
 * @author harovetm
 *
 */
public abstract interface XMLBasedException {

	/**
	 * Get the error code corresponding to the exception
	 * @return
	 */
	public int getErrorCode();
	
	/**
	 * Get the exception wrapped by this exception (optional!)
	 * @return
	 */
	public Exception getOriginatedException();
	
	/**
	 * Get the actual exception name
	 * @return
	 */
	public Exception getBaseException();
	
	/**
	 * Get the customer-friendly message
	 * @return
	 */
	public String getCustomerMessage();
}
