package org.ei.domain;

import org.ei.exception.InfrastructureException;
import org.ei.exception.SystemErrorCodes;

public class PageEntryBuilderException extends InfrastructureException
{

    private static final long serialVersionUID = 7495959734649014117L;

    public PageEntryBuilderException(int code, Exception e) {
        super(code, e);
    }

    public PageEntryBuilderException(String message, Exception e) {
        super(SystemErrorCodes.PAGE_ENTRY_BUILDER_FAILED, message, e);
    }

    public PageEntryBuilderException(Exception e) {
        super(SystemErrorCodes.PAGE_ENTRY_BUILDER_FAILED, e);
    }

}
