package org.ei.domain.personalization;

/** This file is an exception file that is thrown by saved_records component. */

import org.ei.exception.EVBaseException;

public class SavedRecordsException extends EVBaseException {

    private static final long serialVersionUID = 1L;

    public SavedRecordsException(String msg) {
        super(msg);
    }

    public SavedRecordsException(Exception e) {
        super(e);
    }

}
