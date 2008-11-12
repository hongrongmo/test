package org.ei.controller;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.session.UserSession;

public class ControllerClient
{

    private HttpServletRequest request;
    private HttpServletResponse response;
    private UserSession updatedSession;
    private Properties logProps = new Properties();
    private List comments = new ArrayList();
    private String errorResponse;
    private String redirectURL;
    private String contentDispositionFilename;
    private String view;
    public static final int HTTP_HEADER_MAX_LENGTH = 4096;

    public ControllerClient(HttpServletRequest request, HttpServletResponse response)
    {
        this.request = request;
        this.response = response;
    }

    public UserSession getUserSession()
    {
        UserSession u = new UserSession();
        Enumeration en = request.getHeaderNames();
        Properties props = new Properties();
        while(en.hasMoreElements())
        {
            String hKey = (String)en.nextElement();
            if(hKey.indexOf("session") > -1)
            {

                props.setProperty(hKey.toUpperCase(),request.getHeader(hKey));
            }
        }

        u.loadFromProperties(props);
        return u;
    }

    public void addComment(String comment)
    {
        comments.add(comment);
    }

    public void updateUserSession(UserSession u)
    {
        this.updatedSession = u;
    }

    public void log(String logKey, String logValue)
    {
        this.logProps.setProperty(logKey, logValue);
    }

    public void setErrorResponse(String error)
    {
        this.errorResponse = error;
    }

    public void setRedirectURL(String redirectURL)
    {
        this.redirectURL = redirectURL;
    }

    public void setView(String view)
    {
		this.view = view;
	}

    public void setContentDispositionFilename(String strFilename)
    {

        this.contentDispositionFilename = java.net.URLEncoder.encode(strFilename);
    }

    public void setContentDispositionFilenameTimestamp(String strFilename)
    {
        java.util.Calendar calCurrentDate =  java.util.GregorianCalendar.getInstance();
        StringBuffer strbFilename = new StringBuffer();
        strbFilename.append(calCurrentDate.get(java.util.Calendar.DAY_OF_MONTH));
        strbFilename.append("-");
        strbFilename.append(calCurrentDate.get(java.util.Calendar.MONTH) + 1);
        strbFilename.append("-");
        strbFilename.append(calCurrentDate.get(java.util.Calendar.YEAR));
        strbFilename.append("-");
        strbFilename.append(System.currentTimeMillis());
        strbFilename.append("_");
        strbFilename.append(strFilename);

        this.setContentDispositionFilename(java.net.URLEncoder.encode(strbFilename.toString() ));

    }

    public void setRemoteControl()
    {
        if(redirectURL != null)
        {
            response.setHeader("REDIRECT", this.redirectURL);
        }

        if(contentDispositionFilename != null)
        {
            response.setHeader("FILENAME", this.contentDispositionFilename);
        }

        if(errorResponse != null)
        {
            response.setHeader("ERROR", this.errorResponse);
        }

        if(view != null)
        {
			response.setHeader("VIEW", this.view);
		}

        if(comments.size() > 0)
        {
            for(int i=0; i<comments.size();++i)
            {
                //System.out.println("Setting:"+(String)comments.get(i));
                String strcomment = (String)comments.get(i);
                if(strcomment.length() >= HTTP_HEADER_MAX_LENGTH)
                {
                  strcomment = "Comment too long for header.";
                }
                response.setHeader("COMMENT"+i, (String) strcomment);
            }
        }

        Enumeration en = logProps.keys();

        while(en.hasMoreElements())
        {
            String lKey = (String)en.nextElement();
            String strlogdata = (String) logProps.getProperty(lKey);
            if(strlogdata.length() >= HTTP_HEADER_MAX_LENGTH)
            {
              strlogdata = "Log data too long for header.";
            }
            response.setHeader("LOG."+lKey, strlogdata);
        }

        if(updatedSession != null)
        {
            Properties props = updatedSession.unloadToProperties();
            Enumeration enum2 = props.keys();
            while(enum2.hasMoreElements())
            {
                String sKey = (String)enum2.nextElement();
                response.setHeader(sKey, props.getProperty(sKey));
            }
        }

    }

}
