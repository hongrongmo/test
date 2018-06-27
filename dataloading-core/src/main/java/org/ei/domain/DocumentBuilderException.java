package org.ei.domain;

import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;

public class DocumentBuilderException extends InfrastructureException {
    public DocumentBuilderException(int code, String message) {
		super(code, message);
	}

	private static final long serialVersionUID = -175302746243919786L;

    public DocumentBuilderException(int code, Exception e) {
        super(code, e);
    }

    public DocumentBuilderException(String message, Exception e) {
        super(SystemErrorCodes.DOCUMENT_BUILDER_FAILED, message, e);
    }

    public DocumentBuilderException(Exception e) {
        super(SystemErrorCodes.DOCUMENT_BUILDER_FAILED, e);
    }

}
