package org.ei.session;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.util.StringUtil;



public class SessionService extends HttpServlet
{
    private StringUtil sUtil = new StringUtil();
    UserBroker uBroker = new UserBroker();
    SessionBroker sessionBroker;


    public void init()
        throws ServletException
    {
        ServletConfig config = getServletConfig();
        sessionBroker = SessionBroker.getInstance(Long.parseLong(config.getInitParameter("expireIn")));
    }



    public void service(HttpServletRequest request,
                        HttpServletResponse response)
        throws IOException,
               ServletException
    {

        UserSession userSession = null;
        String prodname = request.getParameter("ap");
        String username = request.getParameter("un");
        String password = request.getParameter("pa");
        String ipAddress = request.getParameter("ip");
        String refUrl = request.getParameter("rf");
        String entryToken = request.getParameter("et");
        String sessionID = request.getParameter("si");
        String sessionVersion = request.getParameter("sv");
        String requestType = request.getParameter("rt");


        String status = null;

        if(entryToken != null || username != null)
        {
			sessionID = null;
		}

        try
        {
            ServletOutputStream out = response.getOutputStream();
            if(requestType.equals("g"))
            {


                if(sessionID == null)
                {

                    User u = uBroker.getUser(prodname,
                                             username,
                                             password,
                                             ipAddress,
                                             refUrl,
                                             entryToken);

                	userSession = sessionBroker.createSession(u);
                    userSession.setStatus(SessionStatus.NEW);

                }
                else
                {
                    userSession = sessionBroker.getUserSession(new SessionID(sessionID, Integer.parseInt(sessionVersion)));
                    if(userSession == null)
                    {
                        User u = uBroker.getUser(prodname,
                                                 username,
                                                 password,
                                                 ipAddress,
                                                 refUrl,
                                                 entryToken);

                        userSession = sessionBroker.createSession(u);
                        userSession.setStatus(SessionStatus.NEW_HAD_EXPIRED);
                    }
                    else
                    {
						userSession.setStatus(SessionStatus.OLD_FROM_DATABASE);
					}
                }

                if(userSession.getProperty("ENTRY_TOKEN") == null &&
                   userSession.getUser().isCustomer())
                {
                    /*
                    * It is a customer so load the custom properties.
                    */
                    CustomProperties cprops = new CustomProperties(userSession);
                    userSession = cprops.loadCustom();
                }

                Properties props = userSession.unloadToProperties();
                Enumeration en = props.keys();
                while(en.hasMoreElements())
                {
                    String key = (String)en.nextElement();
                    response.setHeader(key, props.getProperty(key));
                }


                out.print("good");
            }
            else if(requestType.equals("s"))
            {
                UserSession uSession = new UserSession();
                Enumeration en = request.getHeaderNames();
                Properties nProps = new Properties();
                while(en.hasMoreElements())
                {
                    String hkey = ((String)en.nextElement()).toUpperCase();
                    if(hkey.indexOf("SESSION") > -1)
                    {
                        nProps.setProperty(hkey,
                                           request.getHeader(hkey));
                    }
                }

                uSession.loadFromProperties(nProps);
                sessionBroker.updateSession(uSession);
                out.println("Good");
            }
            else if(requestType.equals("t"))
            {
                sessionBroker.touch(sessionID);
            }
            else if(requestType.equals("l"))
            {
				sessionBroker.logout(sessionID);
			}
			else if(requestType.equals("c"))
			{
				String sid = sessionBroker.getActiveSession(entryToken);
				if(sid != null)
				{
					response.setHeader("SESSIONID", sid);
				}
				out.print("good");
			}
			else if(requestType.equals("r"))
			{
				String customerID = uBroker.validateCustomerIP(prodname, ipAddress);
				response.setHeader("CUSTOMERID", customerID);
			}
        }
        catch(Exception e)
        {
            log("Error:",e);
        }
    }
}
