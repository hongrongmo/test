package org.ei.system;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.controller.ControllerClient;
import org.ei.logging.LogClient;
import org.ei.util.GUID;

public class RedirectService extends HttpServlet
{
	
 

	// Logging variables
	private LogClient logClient;
	private String logServiceURL;
	private String appName;
	private String defaultURL;
	
	public void init()
		throws ServletException
	{

		
		ServletConfig config = getServletConfig();

		logServiceURL = config.getInitParameter("logURL");
		appName		  = config.getInitParameter("appName");
		defaultURL    = "http://www.ei.org/";
		if ((logServiceURL != null) && (appName != null) ) { 
			this.logClient = new LogClient(logServiceURL);
		}
		
	}

	
	public void service(HttpServletRequest request, 
			    HttpServletResponse response)
		throws IOException, ServletException
	{
		try
			{
			long start = System.currentTimeMillis();
			int status = HttpServletResponse.SC_MOVED_TEMPORARILY;

			//Getting controllerclient for the requset and response object
			ControllerClient client = new ControllerClient(request, response);
			//This variable is used for url
			String urlString = request.getParameter("URL");

			response.setContentType("text/html");


			PrintWriter out = response.getWriter();

			if ( urlString == null ) {
				urlString = defaultURL;
			}
			
			response.sendRedirect(urlString);

			out.println("<html>");
			out.println("<head><title>RedirectService</title>");
			if ( urlString != null ) {
				out.println("<META HTTP-EQUIV=\"refresh\" content=\"0;URL="+urlString+"\" />"); 
			}
			out.println("</head><body>");

			out.println("<br>Copyright &copy; 2001 by Engineering Information Inc., Hoboken, New Jersey, U.S.A.");
			out.println("</body>");
			out.println("</html>");

			// log information
			if ( this.logClient != null ) {
				this.logClient.reset();
				//logProps.setProperty("reason", reason);

				// puts all parameters into appdata Hashtable getParameterMap in 2.3
				Enumeration paraNames = request.getParameterNames();
				Hashtable ht = new Hashtable();
				long end = System.currentTimeMillis();

				// populate appdata Hashtable
				while (paraNames.hasMoreElements() ) {
					String key = (String) paraNames.nextElement();
					ht.put( key, request.getParameter(key) );	
				}


				// populate mandatory fields for CLF construction
				this.logClient.setdate(start);
				this.logClient.setbegin_time(start);
				this.logClient.setend_time(end);
				this.logClient.setresponse_time(end-start);
				if ( ht.containsKey("EISESSION") ) {
					this.logClient.setsid( (String)ht.get("EISESSION") );
				} else {
					this.logClient.setsid("0");
				}

				this.logClient.setHost(request.getRemoteAddr());
				this.logClient.setrfc931("-");
				this.logClient.setusername("-");				
				this.logClient.setuser_agent(request.getHeader("User-Agent"));
				this.logClient.setHTTPmethod(request.getMethod());
				this.logClient.setreferrer(request.getHeader("referer"));
				this.logClient.seturi_stem(request.getRequestURI());
				this.logClient.seturi_query(request.getQueryString());
				this.logClient.setstatuscode(status);
				this.logClient.setrid( new GUID().toString());
				this.logClient.setappid(this.appName);
				this.logClient.setappdata(ht);
				this.logClient.sendit();
			}
		} catch(Exception e)
		{
			log("RedirectError", e);
			response.sendRedirect(defaultURL);
				
		} 
	}
}
