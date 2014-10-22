package org.ei.controller;

import java.net.UnknownHostException;
import java.security.NoSuchAlgorithmException;
import java.util.Enumeration;
import java.util.Properties;

import org.ei.controller.content.ContentDescriptor;
import org.ei.session.UserSession;
import org.ei.stripes.adapter.IBizBean;
import org.ei.util.GUID;
import org.ei.util.StringUtil;

public class DataRequest {

    private UserSession userSession;
    private Properties requestParameters;
    private ContentDescriptor contentDescriptor;
    private boolean validContentDescriptor;
    private String requestID;
    private long requestTime;
    private String theHash;
    private IBizBean bizbean;

    public DataRequest(UserSession userSession, Properties requestParameters, ContentDescriptor contentDescriptor) throws UnknownHostException  {
            this.requestTime = System.currentTimeMillis();
            this.userSession = userSession;
            this.requestParameters = requestParameters;
            this.contentDescriptor = contentDescriptor;
            if (this.contentDescriptor == null) {
                validContentDescriptor = false;
            } else {
                validContentDescriptor = true;
            }

            if (requestParameters.containsKey("POLL")) {
                this.requestID = requestParameters.getProperty("RID");
            } else if (requestParameters.containsKey("META_REFRESH_POLL")) {
                this.requestID = requestParameters.getProperty("RID");
            } else if (requestParameters.containsKey("COLLECT")) {
                this.requestID = requestParameters.getProperty("RID");
            } else {
                this.requestID = new GUID().toString();
            }
    }

    public DataRequest(UserSession userSession, Properties requestParameters) throws UnknownHostException {
            this.requestTime = System.currentTimeMillis();
            this.userSession = userSession;
            this.requestParameters = requestParameters;

            if (requestParameters.containsKey("POLL")) {
                this.requestID = requestParameters.getProperty("RID");
            } else if (requestParameters.containsKey("META_REFRESH_POLL")) {
                this.requestID = requestParameters.getProperty("RID");
            } else if (requestParameters.containsKey("COLLECT")) {
                this.requestID = requestParameters.getProperty("RID");
            } else {
                this.requestID = new GUID().toString();
            }
    }

    public long getRequestTime() {
        return this.requestTime;
    }

    public String getRequestID() {
        return this.requestID;
    }

    public boolean hasValidContentDescriptor() {
        return validContentDescriptor;
    }

    public void setUserSession(UserSession userSession) {
        this.userSession = userSession;
    }

    public UserSession getUserSession() {
        return this.userSession;
    }

    public void setRequestParameters(Properties requestParameters) {
        this.requestParameters = requestParameters;
        theHash = null;
    }

    public Properties getRequestParameters() {
        return this.requestParameters;
    }

    public void setContentDescriptor(ContentDescriptor contentDescriptor) {
        this.contentDescriptor = contentDescriptor;
    }

    public ContentDescriptor getContentDescriptor() {
        return this.contentDescriptor;
    }

    public String getUniqueHash() throws NoSuchAlgorithmException {

            if (theHash == null) {
                StringBuffer buf = new StringBuffer("B");
                Enumeration<Object> en = this.requestParameters.keys();
                while (en.hasMoreElements()) {
                    String key = (String) en.nextElement();
                    String value = requestParameters.getProperty(key);
                    buf.append(key + value);
                }
                StringUtil util = new StringUtil();
                theHash = util.computeUniqueHash(buf.toString());
            }

        return theHash;
    }

    public IBizBean getBizBean() {
        return bizbean;
    }

    public void setBizBean(IBizBean bizbean) {
        this.bizbean = bizbean;
    }

}
