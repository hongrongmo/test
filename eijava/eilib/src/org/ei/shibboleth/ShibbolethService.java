package org.ei.shibboleth;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.session.SessionCache;
import org.ei.session.UserSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class ShibbolethService extends HttpServlet
{
    private String[] resources = new String[1];
    private String redirURL;
    private ShibbolethIdpMap idpMap = ShibbolethIdpMap.getInstance();
    //Hashtable idpMap = new Hashtable();

    public void init()
        throws ServletException
    {
        ServletConfig config = getServletConfig();
        ServletContext context = getServletContext();
        try
        {
            resources[0] = config.getInitParameter("resource");
        }
        catch(Exception e)
        {
            throw new ServletException(e.getMessage(), e);
        }
    }


    public void printErrorMessage(String message, PrintWriter out)
    {
        out.println("<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8'>");
        out.println("<title>Engineering Village - System error</title></head>");
        out.println("<body bgcolor='#FFFFFF' topmargin='0' marginheight='0' marginwidth='0'>");
        out.println("<table border='0' width='99%' cellspacing='0' cellpadding='0'><tr><td valign='top'><img src='/engresources/images/ev2logo5.gif' border='0'></td></tr><tr><td valign='top' height='5'><img src='/engresources/images/spacer.gif' border='0' height='5'></td></tr><tr><td valign='top' height='2' bgcolor='#3173B5'><img src='/engresources/images/spacer.gif' border='0' height='2'></td></tr><tr><td valign='top' height='10'><img src='/engresources/images/spacer.gif' border='0' height='10'></td></tr></table>");
        out.println("<table border='0' width='99%' cellspacing='0' cellpadding='0' bgcolor='#FFFFFF'><tr><td valign='top' height='15' colspan='3'><img src='/engresources/images/spacer.gif' border='0'></td></tr><tr><td valign='top' width='20'><img src='/engresources/images/spacer.gif' border='0' width='20'></td><td valign='top' colspan='2'></td></tr><tr><td valign='top' height='2' colspan='3'><img src='/engresources/images/spacer.gif' border='0'></td></tr><tr><td valign='top' width='20'><img src='/engresources/images/spacer.gif' border='0' width='20'></td><td valign='top'><A CLASS='MedBlackText'><b>");

        out.println(message);

        out.println("</b></A><br><br></td><td valign='top' width='10'><img src='/engresources/images/spacer.gif' border='0' width='10'></td></tr></table>");
        out.println("<br>");
        out.println("<TABLE WIDTH='100%' CELLSPACING='0' CELLPADDING='0' BORDER='0'><TR><TD><CENTER><A CLASS='MedBlueLink' HREF='/controller/servlet/Controller?CID=ncAboutEI'>About Ei</A> |<a CLASS='MedBlueLink' href='/controller/servlet/Controller?CID=ncAboutEV'>About Engineering Village</a> |<A CLASS='MedBlueLink' HREF='/controller/servlet/Controller?CID=ncFeedback'>Feedback</A>&nbsp;|&nbsp;<A CLASS='MedBlueLink' HREF='/controller/servlet/Controller?CID=ncPrivacyPolicy'>Privacy Policy</A><br><A CLASS='SmBlackText'>&copy; 2008 Elsevier Inc. All rights reserved., </A></CENTER></TD></TR></TABLE>");
        out.println("</body>");
        out.println("</html>");
    }

    public void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {
        String serverName = request.getServerName();
        int serverPort = request.getServerPort();
        if(serverPort != 80)
        {
            serverName = serverName+":"+Integer.toString(serverPort);
        }

		MessageDigest mdigest;
		String secret = "tm789@@#mhlflsdf";
		String atokDigest = null;
		StringBuffer tokVal = new StringBuffer();
		String ipaddress = null;
		boolean isAuthenticated = false;
		boolean isExpired = false;
        String atok    = request.getParameter("atok");
        String custid  = request.getParameter("custid");
		String instant = request.getParameter("instant");
		String idp = request.getParameter("idp");
		String ipAddress = request.getRemoteAddr();

		// check for cookies
		Cookie[] clientCookies = request.getCookies();
		if(clientCookies != null)
		{

			for(int i = 0; i < clientCookies.length; i++)
			{

				Cookie c = clientCookies[i];
				if(c.getName().equals("custid"))
					custid = c.getValue();
				if(c.getName().equals("idp"))
					idp = c.getValue();
			}
		}

		tokVal.append(instant).append(secret).append(idp);

		try
		{
      			mdigest = MessageDigest.getInstance("MD5");
      			byte[] dig = mdigest.digest(tokVal.toString().getBytes());
      			atokDigest = new String(asHex(dig));
      			System.out.println(" HASHED: " + atokDigest);
      	}
      	catch (NoSuchAlgorithmException e)
      	{
      			e.printStackTrace();
      	}

		PrintWriter out = response.getWriter();
		if(atok != null && atokDigest != null)
			isAuthenticated = atok.equals(atokDigest);

		//System.out.println(atok + "versus" + atokDigest);

		if(isAuthenticated)

		{

			try
			{
				DateFormat formatter = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
				String tempDate = instant;
				Date instDate = (Date)formatter.parse(tempDate);
				//System.out.println("DATE : " + tempDate);
				//System.out.println("CURRENT : " + System.currentTimeMillis());
				//System.out.println("INSTANT: " + instDate.getTime());
				//System.out.println("DIFFERENCE: " + (System.currentTimeMillis() - (instDate.getTime())));

				if((System.currentTimeMillis() - (instDate.getTime())) > 3600000)
				{
					isAuthenticated	= false;
					isExpired = true;
				}
			}
			catch(Exception e)
			{
				printErrorMessage("Session creation exception:" +e, out);
			}



			String mappedIdp = (String)idpMap.lookupIdp(custid);

			if(mappedIdp == null || idp == null || !mappedIdp.equals(idp))
			{
				isAuthenticated = false;

			}

			//if(mappedIdp != null && idp != null && mappedIdp.equals(idp))
			if(isAuthenticated)
			{

				System.out.println("SHIBBOLETH AUTHENTICATED: "+ custid);

				try
				{
					SessionCache sCache = SessionCache.getInstance();
					UserSession us = sCache.getUserSession(null,
													   null,
													   custid,
													   null,
													   null,
													   null);

					response.addCookie(new Cookie("custid", custid));
					response.addCookie(new Cookie("idp", idp));
					Cookie cookie = new Cookie("EISESSION", (us.getSessionID()).toString());
					cookie.setMaxAge(-1);
					response.addCookie(cookie);
					response.sendRedirect("http://"+serverName+"/controller/servlet/Controller?EISESSION="+(us.getSessionID()).toString());
				}

				catch(Exception e)
				{
					printErrorMessage("Session creation exception:" +e, out);
				}
			}
			else
			{
				if(!isExpired)
					printErrorMessage("FAILED SHIBBOLETH AUTHENTICATION", out);
				else
					printErrorMessage("EXPIRED SHIBBOLETH AUTHENTICATION. REAUTHENTICATE AT <a href=\"http://cert-evauth.engineeringvillage.com/phptest.php\">Shibboleth Login Page</a> ", out);

			}
        }
        else
        {

			System.out.println("FAILED SHIBBOLETH AUTHENTICATION");
       		System.out.println("ATOK: " + atok);
       		System.out.println("CUSTID: " + custid);
       		System.out.println("INSTANT: " + instant);
       		System.out.println("IDP: " + idp);
       		System.out.println("TOKVAL: " + tokVal);
       		System.out.println("ATOKDIGEST:" + atokDigest);

			printErrorMessage("FAILED SHIBBOLETH AUTHENTICATION", out);
		}
    }

	private String asHex(byte[] hash) {
		StringBuffer buf = new StringBuffer(hash.length * 2);
		int i;

		for (i = 0; i < hash.length; i++) {
			if (((int) hash[i] & 0xff) < 0x10) {
				buf.append("0");
			}
			buf.append(Long.toString((int) hash[i] & 0xff, 16));
		}
		return buf.toString();
	}


}





