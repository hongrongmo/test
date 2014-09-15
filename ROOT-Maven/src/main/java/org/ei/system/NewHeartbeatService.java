package org.ei.system;


import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.ei.config.EVProperties;
import org.ei.config.ApplicationProperties;


@SuppressWarnings("serial")
public class NewHeartbeatService extends HttpServlet
{

	private long startup;
	private String authURL;
	private String appName;
	private String ipAddress;
	private String referrerURL;
	private String username;
	private String password;
	private String dataServiceURL;
	private String email;
	private String fastUrl;

	public void init()
		throws ServletException
	{
		ServletConfig config = getServletConfig();
		this.startup = System.currentTimeMillis();
		authURL = EVProperties.getProperty(ApplicationProperties.AUTH_URL);
		appName = EVProperties.getProperty(EVProperties.APP_NAME);
		fastUrl = EVProperties.getProperty(ApplicationProperties.FAST_BASE_URL);
        dataServiceURL = "http://" + EVProperties.getProperty(ApplicationProperties.DATA_URL) + "/engvillage/servlet/CheckDataService";
        username = config.getInitParameter("username");
        password = config.getInitParameter("password");
        email = config.getInitParameter("email");
	}


	public void service(HttpServletRequest request,
			    HttpServletResponse response)
		throws IOException, ServletException
	{
		ipAddress = request.getRemoteAddr();
		referrerURL = request.getHeader("Referer");
		response.setContentType("text/html");
		Runtime rt = Runtime.getRuntime();
		long totalMemory = rt.totalMemory();
		long freeMemory = rt.freeMemory();

		PrintWriter out = response.getWriter();
		long currentTime = System.currentTimeMillis();
		long uptime = currentTime - startup;
		double uphours = (((uptime/1000)/60)/60);
		CheckEV cEV = new CheckEV();

		cEV.init();
		String server = request.getServerName();
		cEV.setServer(server);
		cEV.setEmail(email);
		cEV.setFastURL(fastUrl);
		InetAddress inetAddress = InetAddress.getLocalHost();
		String ip = inetAddress.getHostAddress();
		out.println("<html>");
		out.println("<head><title>NewHeartbeatService</title></head>");
		out.println("<body>");
		out.println("<br/>Server IP Address :: "+ip);
		out.println("<br/>SERVER :: "+server);

		boolean checkResult = cEV.doSearch(server);
		boolean checkFast = cEV.checkFast();
		boolean checkSearchDatabase = checkDataService(dataServiceURL);
		out.println("<br/><br/>");
		if(!checkFast){

			out.println("<br/><b>FAST is down!!</b>");
		}
		else
		{
			out.println("<br/><b>FAST is UP!!</b>");
		}


		if(!checkSearchDatabase){

			out.println("<br/><b>search database is down!!</b>");

		}
		else
		{
			out.println("<br/><b>search database is UP!!</b>");
		}

		if(checkResult){
			out.println("<br/><b>EV is UP!!</b>");
		}
		else
		{

			out.println("<br/><b>EV is down!!</b>");

		}

		out.println("<br/><br/>");

		if(checkResult && checkFast && checkSearchDatabase)
		{
			out.println("<br>Build: "      + Build.version());
			out.println("<br>China: "      + Build.china());
			out.println("<br>UPTIME: "      + Double.toString(uphours));
			out.println("<br>TOTAL MEMORY: "+ totalMemory);
			out.println("<br>FREE MEMORY: " + freeMemory);
			out.println("<br>Copyright &copy; 2001 by Engineering Information Inc., Hoboken, New Jersey, U.S.A.");
		}
		else
		{
			out.println("<br>Something is wrong<br>");
		}
		out.println("</body>");
		out.println("</html>");
	}

	public boolean checkDataService(String url){
		try{
			HttpClient httpClient = new HttpClient();
			HttpMethod postMethod = new PostMethod(url);
			httpClient.executeMethod(postMethod);
			String response = postMethod.getResponseBodyAsString();
			if(response.trim().equals("UP")){
				return true;
			}
			else{
				return false;
			}
		}
		catch(IOException e)
		{
			return false;
		}

	}
}
