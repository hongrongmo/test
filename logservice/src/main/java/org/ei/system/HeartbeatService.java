package org.ei.system;


import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class HeartbeatService extends HttpServlet
{

	private long startup;

	public void init()
		throws ServletException
	{
		this.startup = System.currentTimeMillis();

	}


	public void service(HttpServletRequest request,
			    HttpServletResponse response)
		throws IOException, ServletException
	{
		response.setContentType("text/html");
		Runtime rt = Runtime.getRuntime();
		long totalMemory = rt.totalMemory();
		long freeMemory = rt.freeMemory();

		PrintWriter out = response.getWriter();
		long currentTime = System.currentTimeMillis();
		long uptime = currentTime - startup;
		double uphours = (((uptime/1000)/60)/60);
		out.println("<html>");
		out.println("<head><title>HeartbeatService</title></head>");
		out.println("<body>");
		out.println("<br>Build: "      + Build.version());
		out.println("<br>China: "      + Build.china());
		out.println("<br>UPTIME: "      + Double.toString(uphours));
		out.println("<br>TOTAL MEMORY: "+ totalMemory);
		out.println("<br>FREE MEMORY: " + freeMemory);
		out.println("<br>Copyright &copy; 2001 by Engineering Information Inc., Hoboken, New Jersey, U.S.A.");
		out.println("</body>");
		out.println("</html>");
	}
}
