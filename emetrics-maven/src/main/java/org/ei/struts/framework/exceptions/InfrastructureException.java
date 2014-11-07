package org.ei.struts.framework.exceptions;

/**
 * Exception thrown when an invalid attempt at logging into the application occurs.
 */
public class InfrastructureException extends BaseException {
    /**
     * 
     */
    private static final long serialVersionUID = 1982126637181944241L;
    
    public InfrastructureException() {

    }
    public InfrastructureException(String message) {
        super(message, null);
    }
    public InfrastructureException(String message, Object[] args) {
        super(message, args);
    }
}
