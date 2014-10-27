package org.ei.session;

import java.io.Serializable;

import org.apache.commons.validator.GenericValidator;
import org.ei.exception.SessionException;
import org.ei.exception.SystemErrorCodes;
import org.ei.util.GUID;

public class SessionID implements Serializable {

    /**
	 *
	 */
    private static final long serialVersionUID = 2114901329265648307L;
    private String ID = "";
    private int versionNumber;

    public SessionID() throws SessionException {
        try {
            this.ID = new GUID().toString();
            this.versionNumber = 1;
        } catch (Exception e) {
            throw new SessionException(SystemErrorCodes.SESSION_CREATE_ERROR, e);
        }
    }

    public SessionID(String sessionID) {
        if (GenericValidator.isBlankOrNull(sessionID)) return;
        String split[] = sessionID.split("_");
        if (split.length > 0) Integer.parseInt(split[0]);
        if (split.length > 1) ID = split[1];
    }

    public SessionID(String ID, int versionNumber) {
        this.ID = ID;
        this.versionNumber = versionNumber;
    }

    public String toString() {
        return Integer.toString(versionNumber) + "_" + ID;
    }

    public int incrementVersion() {
        this.versionNumber++;
        return this.versionNumber;
    }

    public int getVersionNumber() {
        return this.versionNumber;
    }

    public String getID() {
        return this.ID;
    }

}