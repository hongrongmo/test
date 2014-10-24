package org.ei.logging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLConnection;
import java.util.Hashtable;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.util.StringUtil;

/**
 *  Description of the Class
 *
 *@author     dbaptist
 *@created    November 29, 2001
 */
public class LogService extends HttpServlet {
	/**
	 *  Description of the Field
	 *
	 *@since
	 */
	protected URLConnection conn;
	/**
	 *  Description of the Field
	 *
	 *@since
	 */
	protected PrintWriter ps;
	/**
	 *  Description of the Field
	 *
	 *@since
	 */
	protected BufferedReader is;
	private StringUtil sUtil = new StringUtil();
	private String poolConf;


	/**
	 *  Description of the Method
	 *
	 *@param  request               Description of Parameter
	 *@param  response              Description of Parameter
	 *@exception  IOException       Description of Exception
	 *@exception  ServletException  Description of Exception
	 *@since
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			 throws IOException, ServletException {

		ServletOutputStream out = response.getOutputStream();
		out.println("Working....");

		//URL myNewURL;
		LogClient log = new LogClient("http://localhost/logservice/servlet/LogServer");
		log.setappid("test");
		log.setHost(request.getRemoteAddr());
		log.setcust_id(1);
		log.setrfc931("-");
		log.setusername("-");
		log.setHTTPmethod(request.getMethod());
		log.setdate(System.currentTimeMillis());
		log.setbegin_time(System.currentTimeMillis());
		log.setend_time(System.currentTimeMillis());
		log.seturi_stem(request.getRequestURI());
		log.seturi_query(request.getQueryString());
		log.setuser_agent(request.getHeader("User-Agent"));
		log.setbytes(request.getContentLength());
		log.setstatuscode(200);
		log.setquery_string("{}");
		Hashtable h = new Hashtable();
		h.put("appdata1", new String("appvalue1"));
		h.put("appdata2", new String("appvalue2"));

		log.setappdata(h);
		log.sendit();

	}

}

