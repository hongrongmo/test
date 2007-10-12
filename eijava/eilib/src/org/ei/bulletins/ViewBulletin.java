package org.ei.bulletins;


import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import org.ei.session.*;
import org.ei.controller.ControllerClient;
import org.ei.util.StringUtil;
import org.ei.util.*;
import java.util.*;
import java.net.*;
import java.io.*;
import org.ei.logging.*;
import org.ei.controller.*;


public class ViewBulletin extends HttpServlet
{
    private SessionCache sCache = null;
	String bulletinFileLocation;
	String logURL;
	String appName;
	String authURL;
    ControllerClient client;
    ServletConfig config;

    public void init()
        throws ServletException
    {
        config = getServletConfig();
		authURL = config.getInitParameter("authURL");
		bulletinFileLocation = config.getInitParameter("bulletinFileLocation");
		logURL = config.getInitParameter("logURL");
		appName = config.getInitParameter("appName");

    }


    public void service(HttpServletRequest request,
                        HttpServletResponse response)
        throws IOException,
               ServletException
    {

        UserSession userSession = null;

        String username = null;
        String password = null;
        String ipAddress = null;
        String referrerURL = null;
        String entryToken = null;
        String sessionID = null;
        BufferedInputStream bis = null;
		BufferedOutputStream bout = null;
		File bulletinFile = null;

		sCache = SessionCache.getInstance(authURL,appName);

        try
        {
            client = new ControllerClient(request, response);
			javax.servlet.http.Cookie [] cookies = request.getCookies();
			SessionID sesID = null;
			LogClient logClient = new LogClient(logURL);
			Hashtable logProperties = new Hashtable();

			if(cookies != null)
			{
				for(int i=0; i<cookies.length;++i)
				{
					javax.servlet.http.Cookie cookie = cookies[i];
					if(cookie.getName().equals("EISESSION"))
					{
						sessionID = cookie.getValue();
						break;
					}
				}
			}

			if(sessionID == null)
			{
				sessionID = request.getParameter("EISESSION");
			}
			if(sessionID != null)
			{
				sesID = new SessionID(sessionID);
			}

			referrerURL = request.getHeader("Referer");
			String ipaddress = request.getRemoteAddr();
			entryToken = request.getParameter("SYSTEM_ENTRY_TOKEN");

			UserSession ussession = sCache.getUserSession(sesID, ipaddress, referrerURL, username, password,entryToken);

			String status = ussession.getStatus();

			if(status.equals(SessionStatus.NEW_HAD_EXPIRED))
			{
				String serverName = request.getServerName();
				int serverPort = request.getServerPort();
				if(serverPort != 80)
				{
					serverName = serverName+":"+Integer.toString(serverPort);
				}
				response.sendRedirect("http://"+serverName+"/controller/servlet/Controller?SYSTEM_LOGOUT=true&CID="+Controller.REDIR_PAGE_SESSION_EXPIRED);
			}
			else
			{

				User user = ussession.getUser();

				String cartridges[] = user.getCartridge();
				StringBuffer buffCartridges = new StringBuffer();

				for (int i = 0; i < cartridges.length; i++)
				{
					buffCartridges.append(cartridges[i]);

					if(i > 0)
						buffCartridges.append(";");
				}
				String lcCartridges = buffCartridges.toString().toLowerCase();
				if (lcCartridges.indexOf("lit_htm") == -1 && lcCartridges.indexOf("lit_pdf") == -1
				&& lcCartridges.indexOf("pat_htm") == -1 && lcCartridges.indexOf("pat_pdf") == -1 )
				{
						throw new SecurityException("<DISPLAY>You do not have access to the bulletins.</DISPLAY>");
				}

				String id = request.getParameter("id");
				String cType = request.getParameter("cType");

				BulletinBuilder builder = new BulletinBuilder();

				BulletinLinkBuilder linkBuilder = new BulletinLinkBuilder();

				Bulletin bulletin = builder.buildBulletinDetail(id);

				client.log("EISESSION", sesID.toString());
				client.log("request", "viewBulletin");
				client.log("dbname", bulletin.getDatabase());
				client.log("category",bulletin.getCategory());
				client.log("filename", bulletin.getFileName());
				client.log("custid", user.getCustomerID());
				client.log("ctype",cType);
				client.setRemoteControl();

				bulletin.setContentType(cType);
				StringBuffer link = new StringBuffer(linkBuilder.buildLink(bulletin));

				if(cType.equals("HTML"))
				{
					response.setContentType("text/html");
				}
				else if (cType.equals("PDF"))
				{
					response.setContentType("application/pdf");
					}
				else if (cType.equals("ZIP"))
				{
					response.setContentType("application/zip");
					StringBuffer bufHeader = new StringBuffer("attachments; ");
					bufHeader.append("filename=").append(bulletin.getFileName()).append(".zip");
					response.setHeader("Content-disposition", bufHeader.toString());
				}
				else if(cType.equals("SAVEHTML"))
				{
					response.setContentType("text/html");
					StringBuffer bufHeader = new StringBuffer("attachments; ");
					bufHeader.append("filename=").append(bulletin.getFileName()).append(".htm");
					response.setHeader("Content-disposition", bufHeader.toString());
				}
				else if (cType.equals("SAVEPDF"))
				{
					response.setContentType("application/pdf");
					StringBuffer bufHeader = new StringBuffer("attachments; ");
					bufHeader.append("filename=").append(bulletin.getFileName()).append(".pdf");
					response.setHeader("Content-disposition", bufHeader.toString());
				}

				StringBuffer fullUrl = new StringBuffer();
				String fullLink = fullUrl.append(bulletinFileLocation).append("/").append(link).toString();

				bulletinFile = new File(fullLink);

				bis = new BufferedInputStream(new FileInputStream(bulletinFile));
				bout = new BufferedOutputStream(response.getOutputStream());

				byte[] buff = new byte[1024];
				int bytesRead;

				while(-1 != (bytesRead = bis.read(buff, 0, buff.length)))
				{
					bout.write(buff, 0, bytesRead);
				}
				bis.close();
				bout.close();

				//Log it

				long start = System.currentTimeMillis();

				client.log("REMOTEADDRESS",request.getRemoteAddr());
				client.log("User-Agent",request.getHeader("User-Agent"));
				client.log("METHOD",request.getMethod());
				client.log("REFERER",request.getHeader("referer"));
				client.log("URL",request.getRequestURI());
				client.log("QUERYSTRING",request.getQueryString());
				client.log("STATUSCODE",String.valueOf(HttpServletResponse.SC_OK));
				client.log("RID",new GUID().toString());
				client.log("APPID",appName);
				client.setRemoteControl();

				if(logProperties.containsKey("EISESSION"))
				{
					client.log("SID",(String) logProperties.get("EISESSION"));
				}
				else
				{
					client.log("SID","0");
				}
				long end = System.currentTimeMillis();
				client.log("END_TIME",String.valueOf(end));
				client.log("RESPONSE_TIME",String.valueOf(end - start));
				client.setRemoteControl();
			}
		}
		catch(Exception e)
		{
			client.log("Error:",e.getMessage());
			client.setRemoteControl();
		}
		finally
		{
			if(bis != null)
			{
				try
				{
					bis.close();
				}
				catch(Exception e)
				{
					client.log("Error:",e.getMessage());
					client.setRemoteControl();
				}
			}
			if(bout != null)
			{
				try
				{
					bout.close();
				}
				catch(Exception e)
				{
					client.log("Error:",e.getMessage());
					client.setRemoteControl();
				}
			}
		}
	}
}


