package org.ei.athens;


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


import net.athensams.client.*;

public class AthensService extends HttpServlet
{

    private ClientConfiguration athensConf;
    private String dspid;
    private String[] resources = new String[1];
    private String athensBaseURL;
    private String redirURL;


    public void init()
        throws ServletException
    {
        ServletConfig config = getServletConfig();
        ServletContext context = getServletContext();
        try
        {
			System.out.println("Creating athens agent...");
            athensConf = new ClientConfiguration(context.getRealPath("/WEB-INF/athens_agent_conf.txt"));
            dspid = config.getInitParameter("dspid");
            resources[0] = config.getInitParameter("resource");
            athensBaseURL = config.getInitParameter("athensBaseURL");
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
        out.println("<TABLE WIDTH='100%' CELLSPACING='0' CELLPADDING='0' BORDER='0'><TR><TD><CENTER><A CLASS='MedBlueLink' HREF='/controller/servlet/Controller?CID=ncAboutEI'>About Ei</A> |<a CLASS='MedBlueLink' href='/controller/servlet/Controller?CID=ncAboutEV'>About Engineering Village</a> |<A CLASS='MedBlueLink' HREF='/controller/servlet/Controller?CID=ncFeedback'>Feedback</A>&nbsp;|&nbsp;<A CLASS='MedBlueLink' HREF='/controller/servlet/Controller?CID=ncPrivacyPolicy'>Privacy Policy</A><br><A CLASS='SmBlackText'>&copy; 2009 Elsevier Inc. All rights reserved., </A></CENTER></TD></TR></TABLE>");
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



        String athensUser    = request.getParameter("ath_user");
        String athensTToken  = request.getParameter("ath_ttok");
		String ipAddress = request.getHeader("x-forwarded-for");
		if(ipAddress == null)
		{
			ipAddress = request.getRemoteAddr();
		}



        PrintWriter out = response.getWriter();

        if(athensUser == null)
        {
            String returnURL = "http://"+serverName+"/controller/servlet/AthensService";
            redirURL = athensBaseURL+"/?ath_returl="+URLEncoder.encode(returnURL)+"&ath_dspid="+dspid;
//            System.out.println(redirURL);
            response.sendRedirect(redirURL);
        }
        else
        {
            try
            {


				Authenticated auth = athensConf.checkToken(athensUser,
													 athensTToken,
													 ipAddress,
													 resources );

                if(auth.getCanAccess(resources[0]))
                {
                    String institution = auth.getOrganisationNum();
                    System.out.println("Success!!!!"+institution);
                    SessionCache sCache = SessionCache.getInstance();
                    UserSession us = sCache.getUserSession(null,
                                                           ipAddress,
                                                           institution.toLowerCase(),
                                                           null,
                                                           null,
                                                           null);


                    Cookie cookie = new Cookie("EISESSION", (us.getSessionID()).toString());
                    cookie.setMaxAge(-1);
                    response.addCookie(cookie);
                    response.sendRedirect("http://"+serverName+"/controller/servlet/Controller?EISESSION="+(us.getSessionID()).toString());
                    auth.log(resources);
                }
                else
                {
                    printErrorMessage("You do not have access to EV2", out);
                }
            }
			catch ( InvalidUsernameException e )
			{
			    printErrorMessage("Error: Username invalid", out);
			}
			catch ( InvalidIPaddressException e )
			{
				printErrorMessage("Error: IP Address invalid", out);
			}
			catch ( InvalidTokenException e )
			{
				printErrorMessage("Error: Token has expired, or is invalid", out);
			}
			catch ( NoResourceAccessException e )
			{
				printErrorMessage("Error: Token validated OK, but resource access denied", out);

			}
			catch ( CannotCommunicateException e )
			{
				printErrorMessage("Communication problem: " + e, out);
			}
			catch(Exception e)
			{
				printErrorMessage("Session creation exception:" +e, out);
			}
        }
    }
}





