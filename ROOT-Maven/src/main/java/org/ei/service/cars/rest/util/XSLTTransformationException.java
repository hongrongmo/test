package org.ei.service.cars.rest.util;

public class XSLTTransformationException extends Exception {

    private static final long serialVersionUID = 1085991030060813871L;

    public XSLTTransformationException(Exception e) {
        super(e);
    }

    public XSLTTransformationException(String message, Exception e) {
        super(message, e);
    }

    public XSLTTransformationException(String message, Error e) {
        super(message, e);
    }

    public XSLTTransformationException(String message, Throwable t) {
        super(message, t);
    }

}
