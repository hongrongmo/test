/*
 * $Header:   P:/VM/ei-dev/archives/Back Office/webapps/backoffice/WEB-INF/src/java/org/ei/struts/backoffice/credentials/username/Username.java-arc   1.1   Apr 01 2009 10:52:14   johna  $
 * $Revision:   1.1  $
 * $Date:   Apr 01 2009 10:52:14  $
 *
 * ====================================================================
 */


package org.ei.struts.backoffice.credentials.username;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.struts.backoffice.Constants;
import org.ei.struts.backoffice.Tokens;
import org.ei.struts.backoffice.credentials.Credentials;

/**
 *
 * @author $Author:   johna  $
 * @version $Revision:   1.1  $ $Date:   Apr 01 2009 10:52:14  $
 */
public class Username extends Credentials {

    private static Log log = LogFactory.getLog("Username");

    private String m_strUsername = null;
    private String m_strPassword = null;

    public String getUsername() { return m_strUsername ; }
    public void setUsername(String username) { m_strUsername =  (username != null) ? username.trim() : null; }

    public String getPassword() { return m_strPassword ; }
    public void setPassword(String password) { m_strPassword = (password != null) ? password.trim() : null; }

    public Map getLinkParameters() {

        Map map = super.getLinkParameters();
        map.put(Tokens.ACCESS, Constants.USERNAME);

        return map;
    }

    public String getType() { return Constants.USERNAME; }

    public String getDisplayString() {

        StringBuffer strBuf = new StringBuffer();
        strBuf.append(getUsername());
        strBuf.append(" / ");
        strBuf.append(getPassword());

        return strBuf.toString();
    }


}
