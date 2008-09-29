package org.ei.controller;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.controller.content.ContentConfig;
import org.ei.controller.content.ContentDescriptor;
import org.ei.controller.logging.LogEntry;
import org.ei.controller.logging.Logger;
import org.ei.session.SessionCache;
import org.ei.session.SessionID;
import org.ei.session.SessionStatus;
import org.ei.session.User;
import org.ei.session.UserSession;
import org.ei.util.*;


public class Controller extends HttpServlet
{

    public static String REDIR_PAGE_INVALID_CID = "invalidCID";
    public static String REDIR_TOKEN_EXPIRED = "tokenExpired";
    public static String REDIR_TOKEN_CONCURRENT = "tokenConcurrent";
    public static String REDIR_PAGE_GENERAL_EXCEPTION = "generalException";
    public static String HOME_CID = "home";
    public static String XML_CID = "openXML";
    public static String REDIR_PAGE_SESSION_EXPIRED = "endSession";
    private String configFile;
    private String appName;
    private Logger logger;
    private String dataCacheDir;
    private String authURL;
    private String lhlURL;
    private SessionCache sCache;
    private Map ipBypass = new HashMap();


    private boolean appendSession;

    public void init()
        throws ServletException
    {
        try
        {
            ServletConfig config = getServletConfig();
            configFile = config.getInitParameter("contentConfig");
            dataCacheDir = config.getInitParameter("dataCacheDir");
            authURL = config.getInitParameter("authURL");
            appName = config.getInitParameter("appName");
            lhlURL = config.getInitParameter("lindahallURL");
            sCache = SessionCache.getInstance(authURL,
                              appName);

            appendSession = new Boolean(config.getInitParameter("appendSession")).booleanValue();
            logger = new Logger(config.getInitParameter("logURL"));
            /* App calling itself */
            this.ipBypass.put("127.0.0.1", "y");
			/* Refworks */
			this.ipBypass.put("207.158.24.2","y");
			this.ipBypass.put("207.158.24.3", "y");
			this.ipBypass.put("207.158.24.4", "y");
			this.ipBypass.put("207.158.24.5", "y");
			this.ipBypass.put("207.158.24.6", "y");
			this.ipBypass.put("207.158.24.7", "y");
			this.ipBypass.put("207.158.24.8", "y");
			this.ipBypass.put("207.158.24.10", "y");
			this.ipBypass.put("207.158.24.11", "y");
			this.ipBypass.put("207.158.24.12", "y");
			this.ipBypass.put("207.158.24.13", "y");
			this.ipBypass.put("207.158.24.14", "y");
			this.ipBypass.put("207.158.24.15", "y");
			this.ipBypass.put("207.158.24.16", "y");
			this.ipBypass.put("207.158.24.17", "y");
			this.ipBypass.put("207.158.24.18", "y");
			this.ipBypass.put("207.158.24.19", "y");
			this.ipBypass.put("207.158.24.20", "y");
			this.ipBypass.put("207.158.24.21", "y");
			this.ipBypass.put("207.158.24.22", "y");
			this.ipBypass.put("207.158.24.23", "y");
			this.ipBypass.put("207.158.24.24", "y");
			this.ipBypass.put("207.158.24.25", "y");
			this.ipBypass.put("207.158.24.26", "y");
			this.ipBypass.put("207.158.24.27", "y");
			this.ipBypass.put("207.158.24.28", "y");
			this.ipBypass.put("207.158.24.29", "y");
			this.ipBypass.put("207.158.24.30", "y");

			/* Web of Science, calling for IBM */

			this.ipBypass.put("84.18.184.16", "y");
			this.ipBypass.put("170.107.188.20", "y");
        }
        catch(Exception e)
        {
            throw new ServletException(e.getMessage(), e);
        }
    }

    public void destroy()
    {
        try
        {
            logger.shutdown();
        }
        catch(Exception e)
        {
            // nothing here yet
        }
    }

    public void service(HttpServletRequest request,
                        HttpServletResponse response)
        throws IOException, ServletException
    {
		String uagent = request.getHeader("User-Agent");
		if(uagent != null &&
		   uagent.indexOf("PEAR") > -1)
		{
			return;
		}




		try
		{
			String ip = request.getHeader("x-forwarded-for");
			if(ip == null)
			{
				ip = request.getRemoteAddr();
			}
			IPBlocker ipBlocker = IPBlocker.getInstance();
			if(ipBlocker.block(ip))
			{
				System.out.println("Blocking:"+ip);
				return;
			}
		}
		catch(Exception e)
		{
			throw new ServletException("Controller",e);
		}


		try
		{
			if(RssHandler.handledRequest(request, response))
			{
				return;
			}
		}
		catch(Exception e)
		{
			throw new ServletException("Controller",e);
		}

		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		if(serverPort != 80)
		{
			serverName = serverName+":"+Integer.toString(serverPort);
		}

		if((request.getHeader("referer") == null || request.getHeader("referer").indexOf(serverName)== -1) &&
		   (request.getParameter("CID") != null &&
		   (request.getParameter("CID").equals("quickSearchCitationFormat") || request.getParameter("CID").equals("expertSearchCitationFormat"))))
		{
			return;
		}

		OutputPrinter printer = new OutputPrinter(response,
												  appendSession,
												  serverName);

		if(request.getAttribute("cache") != null)
		{
			printer.setCacheID((String)request.getAttribute("cache"));
		}

		DataRequest dataRequest = null;
		DataResponse dataResponse = null;
		LogEntry logEntry = null;
		UserSession usess = null;


		/*
		*	 Get the usersession.
		*/
		try
		{
			usess = getUserSession(request,
								   response,
								   printer,
								   serverName);

			try
			{
				if(CookieHandler.handleRequest(request,response,usess))
				{
					return;
				}
			}
			catch(Exception e)
			{
				log("Error in cookie handler", e);
			}
		}
		catch(Exception e)
		{
			log("Error in getting user session", e);
		}


		try
		{

			String tokenConcurrent = usess.getProperty("token_concurrent");
			String tokenExpired = usess.getProperty("token_expired");

			String contentID = request.getParameter("CID");
			if(usess.getProperty("ENV_SESSION_END_REDIR").equals("yes"))
			{
				usess.setProperty("ENV_SESSION_END_REDIR", "no");

				if(contentID != null &&
				   contentID.equals(XML_CID))
				{
					printer.printXMLSessionExpired();
				}
				else
				{
					printer.printSessionExpiredRedirect(usess);
				}
			}
			else if(tokenConcurrent != null &&
					!tokenConcurrent.equals(""))
			{
				usess.setProperty("token_concurrent", "");
				printer.printTokenConcurrentRedirect(tokenConcurrent, usess);
			}
			else if(tokenExpired != null &&
					!tokenExpired.equals(""))
			{
				usess.setProperty("token_expired", "");
				printer.printTokenExpiredRedirect(tokenExpired, usess);
			}
			else
			{
				dataRequest = getDataRequest(usess, request);

				if(!dataRequest.hasValidContentDescriptor())
				{
					/*
					*   Not a valid contentID Error
					**/
					printer.printInvalidCIDRedirect(usess);
					return;
				}

				DataResponseBroker resBroker = DataResponseBroker.getInstance(dataCacheDir);
				dataResponse = resBroker.getDataResponse(printer, dataRequest);
				logEntry = getLogEntry(dataResponse,request);
				usess = dataResponse.getUpdatedSession();
			}
		}
		catch(Exception e)
		{
			log("Error", e);

			if(e.getMessage() != null && e.getMessage().equals("SHUTDOWN"))
			{
				printer.printFatalError();
				System.out.println("Shutting down");
				System.exit(-1);
			}

			if(usess == null || ((dataRequest.getContentDescriptor()).getContentID()).equals(REDIR_PAGE_GENERAL_EXCEPTION))
			{
				printer.printFatalError();
			}
			else
			{
				printer.printGeneralExceptionRedirect(e, usess);
			}
		}
		finally
		{
			try
			{
				printer.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			//Log and clean up;
			if(logEntry != null)
			{
				logger.enqueue(logEntry);
			}

			if(usess != null)
			{
				try
				{
					sCache.touch(usess.getSessionID());
				}
				catch(Exception e1)
				{
					log("Error", e1);
				}
			}
		}
    }



    protected LogEntry getLogEntry(DataResponse dataResponse,
                                   HttpServletRequest request)
    {
        LogEntry entry = new LogEntry();
        Properties props = dataResponse.getLogProperties();

  		String contractID = ((dataResponse.getUpdatedSession()).getUser()).getContractID();
  		String entryToken = (dataResponse.getUpdatedSession()).getProperty("ENTRY_TOKEN");

		if(entryToken != null)
		{
			props.put("etoken", entryToken);
		}

		if(contractID != null)
		{
			props.put("contract", contractID);
		}


		/*
		*	Add the department and clientID cookies to the log props.
		*/

		Cookie[] cookies = request.getCookies();
		if(cookies != null)
		{
			for(int i=0; i<cookies.length;++i)
			{
				Cookie cookie = cookies[i];
				if(cookie.getName().equals("DEPARTMENT"))
				{
					String department = cookie.getValue();
					props.put("department", department);
				}
				else if(cookie.getName().equals("CLIENTID"))
				{
					String clientID = cookie.getValue();
					props.put("clientid", clientID);
				}
			}
		}

        entry.setLogProperties(props);
        entry.setContentID(((dataResponse.getDataRequest()).getContentDescriptor()).getContentID());
        entry.setRequestTime((dataResponse.getDataRequest()).getRequestTime());
        entry.setRequestID((dataResponse.getDataRequest()).getRequestID());
        entry.setResponseTime(dataResponse.getResponseTime());
        entry.setCustomerID(((dataResponse.getUpdatedSession()).getUser()).getCustomerID());
        entry.setUsername(((dataResponse.getUpdatedSession()).getUser()).getUsername());
        entry.setSessionID(((dataResponse.getUpdatedSession()).getSessionID()).toString());
        entry.setUserAgent(request.getHeader("User-Agent"));
        entry.setRefURL(request.getHeader("referer"));
        entry.setAppName(appName);
        entry.setURLStem(request.getRequestURI());
        entry.setURLQuery(request.getQueryString());

        String ipAddress = null;
		String loc = System.getProperty("loc");

		if(loc != null &&
		   loc.equals("china"))
		{
			ipAddress = request.getRemoteAddr();
		}
		else
		{
			ipAddress = request.getHeader("x-forwarded-for");
			if(ipAddress == null)
			{
				ipAddress = request.getRemoteAddr();
			}
		}


        entry.setIPAddress(ipAddress);
        return entry;
    }

    protected LogEntry getLogEntry(DataRequest dataRequest)
    {
        LogEntry entry = new LogEntry();
        return entry;
    }



    protected UserSession getUserSession(HttpServletRequest request,
                                         HttpServletResponse response,
                                         OutputPrinter printer,
                                         String serverName)
        throws Exception
    {
        UserSession us = null;
        String sessionID = null;
        SessionID sesID = null;
        boolean dayPass = false;


        /**
        *   First try and get the session from the cookie.
        **/

        Cookie[] cookies = request.getCookies();
        if(cookies != null)
        {
            for(int i=0; i<cookies.length;++i)
            {
                Cookie cookie = cookies[i];
                if(cookie.getName().equals("EISESSION"))
                {
                    sessionID = cookie.getValue();
                }
                else if(cookie.getName().equals("DAYPASS"))
                {
					dayPass = true;
				}
            }
        }

        /**
        *   If the client did not except cookies then use the session from the request parameter
        **/


        if(sessionID == null)
        {
            printer.setAppendSession(true);
        }
        else
        {
            printer.setAppendSession(false);
        }

        String useSessionParam = "";
        if(request.getParameter("SYSTEM_USE_SESSION_PARAM") != null)
        {
            useSessionParam = request.getParameter("SYSTEM_USE_SESSION_PARAM");
        }

        if(sessionID == null || useSessionParam.equals("true"))
        {
            sessionID = request.getParameter("EISESSION");
        }

        if(sessionID != null)
        {
            sesID = new SessionID(sessionID);
        }

        String referrerURL = request.getHeader("Referer");

		String ipAddress = null;
		String loc = System.getProperty("loc");

		if(loc != null &&
		   loc.equals("china"))
		{
			ipAddress = request.getRemoteAddr();
		}
		else
		{
			ipAddress = request.getHeader("x-forwarded-for");
			if(ipAddress == null)
			{
				ipAddress = request.getRemoteAddr();
			}
		}


        String username = request.getParameter("SYSTEM_USERNAME");
        String password = request.getParameter("SYSTEM_PASSWORD");
        String entryToken = request.getParameter("SYSTEM_ENTRY_TOKEN");

        if(entryToken != null)
        {
			printer.setEntryToken(entryToken);
		}


        String logout = request.getParameter("SYSTEM_LOGOUT");

        /**
        *   Get the UserSession from the sessioncache
        **/

        if(request.getParameter("SYSTEM_NEWSESSION") != null)
        {
            sesID = null;
        }

        us = sCache.getUserSession(sesID,
        			               ipAddress,
        			               referrerURL,
        			               username,
        			               password,
        			               entryToken);

		if(us.getUser().getUsername().equals("_IP") &&
		   !ipGood(us.getUser().getIpAddress(), ipAddress, us.getUser().getCustomerID()))
		{
			System.out.println("Bad IP:"+us.getUser().getIpAddress()+" : "+ipAddress+" : "+us.getUser().getCustomerID());
			throw new Exception("Security Violation:"+ipAddress);
		}

        if(logout != null && logout.equals("true"))
        {
            us = sCache.logout(us);
            us = sCache.getUserSession(null,
                           			   ipAddress,
                           			   referrerURL,
                           			   username,
                           			   password,
                           			   entryToken);
        }

        if(us.getStatus().equals(SessionStatus.NEW_HAD_EXPIRED))
        {
            // The session has just expired.

            if(request.getParameter("CID") == null ||
               request.getParameter("SYSTEM_PT") != null)
            {

                /*
                *	There are two situations where user can pass through when
                *	Session has expired.
                *	1) If they are coming to the homepage
                *	2) If the http parameter SYSTEM_PT is used.
                */

                us = sCache.getUserSession(null,
                               			   ipAddress,
                               			   referrerURL,
                               			   username,
                               			   password,
                               			   entryToken);

                us.setProperty("ENV_SESSION_END_REDIR", "no");
            }
            else
            {
                //Make them go to the SessionEnded screen.

                us.setProperty("ENV_SESSION_END_REDIR", "yes");
            }
        }
        else
        {
            us.setProperty("ENV_SESSION_END_REDIR", "no");
        }

        us.setProperty("ENV_IPADDRESS", ipAddress);
        us.setProperty("ENV_LHLURL", lhlURL);
        us.setProperty("ENV_BASEADDRESS", serverName);
        String userAgent = request.getHeader("User-Agent");
        if(userAgent != null)
        {
            us.setProperty("ENV_UAGENT", userAgent);
        }

        if(dayPass)
        {
			us.setProperty("ENV_DAYPASS", "T");
		}

        return us;

    }

	private boolean ipGood(String originalIP,
						   String currentIP,
						   String customerID)
		throws Exception
	{

		if(this.ipBypass.containsKey(originalIP) ||
		   this.ipBypass.containsKey(currentIP))
		{
			return true;
		}

		String oip[] = originalIP.split("\\.");
		String cip[] = currentIP.split("\\.");
		if(oip[0].equals(cip[0]) && oip[1].equals(cip[1]))
		{
			return true;
		}
		else
		{
			String testCustomerID = sCache.validateCustomerIP(currentIP);
			return customerID.equals(testCustomerID);
		}
	}

    /*
    *   this method handles the content customization / auth for
    *   requests.
    *
    */

    protected DataRequest getDataRequest(UserSession usess, HttpServletRequest request)
        throws Exception
    {

        ContentConfig contentConfig = ContentConfig.getInstance(this.configFile);

        User u = usess.getUser();
        String contentID = request.getParameter("CID");
        if(contentID == null)
        {
            contentID = HOME_CID;
        }

        ContentDescriptor cd = null;

        /*
        *   Look for customized content.
        */

        if(u.isCustomer())
        {
            String customerID = u.getCustomerID();
            cd = contentConfig.getContentDescriptor(customerID, contentID);
        }
        else
        {
            cd = contentConfig.getContentDescriptor(contentID);
        }

        /*
        *   We have the ContentDescriptor, now lets get the RequestParameters.
        */

        Enumeration en = request.getParameterNames();
        Properties requestParams = new Properties();
        while(en.hasMoreElements())
        {
            String key = (String)en.nextElement();
            if(key.indexOf("SYSTEM") != 0) {
                requestParams.put(key, request.getParameterValues(key));
            }
        }


        return new DataRequest(usess, requestParams, cd);
    }
}

