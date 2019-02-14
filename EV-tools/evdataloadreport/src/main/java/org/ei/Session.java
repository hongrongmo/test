package org.ei;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.JTextField;

/**
 * Servlet implementation class Session
 */
public class Session extends HttpServlet {
	private static final long serialVersionUID = 1L;
     
	String url = null;
	String dbPassword = null;
	String operation = null;
	String oracleUsername = null;
	String osUser = null;

	
	String paramName = null;
	
	ArrayList <Map<String,String>> sessionInfoList = new ArrayList<Map<String,String>>();
	ArrayList <Map<String,String>> sessionCountList = new ArrayList<Map<String,String>>();
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Session() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		
		try
		{

			if(session !=null && session.getAttribute("url") !=null
					&& session.getAttribute("dbpwd") !=null && request.getParameter("op") !=null)
			{
				url = session.getAttribute("url").toString();
				dbPassword = session.getAttribute("dbpwd").toString();
				operation = request.getParameter("op").toString();
				
				session.setAttribute("OPERATION", operation);
				
				
				if(operation.equalsIgnoreCase("usersession"))
				{
					oracleUsername = request.getParameter("oracleUserName").toUpperCase().trim();
					session.setAttribute("SESSIONNAME", oracleUsername);
				}
				else if (operation.equalsIgnoreCase("oousersession"))
				{
					oracleUsername = request.getParameter("oracleUserName").toUpperCase().trim();
					
					osUser =  request.getParameter("osUserName").toUpperCase().trim();
					session.setAttribute("SESSIONNAME", oracleUsername);
					session.setAttribute("OSUSER", osUser);
				}
				
				else if(operation.equalsIgnoreCase("showparam"))
				{
					paramName = request.getParameter("parameterName").toLowerCase().trim();
					session.setAttribute("PARAMETERNAME", paramName);
					
				}
				
			
				
				if(sessionInfoList.size() >0)
				{
					sessionInfoList.clear();
				}
				
					SessionInfo sessionInfo = new SessionInfo(url,dbPassword,operation,oracleUsername,osUser,paramName); 
					sessionInfoList = sessionInfo.getSessionInfo();
					
					if(operation.equalsIgnoreCase("all") && sessionInfoList !=null)
					{
						session.setAttribute("ALLSESSION", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
						
					}
					else if(operation.equalsIgnoreCase("allcount") && sessionInfoList !=null)
					{
						session.setAttribute("ALLSESSIONCOUNT", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
						
					}
					
					else if(operation.equalsIgnoreCase("usersession") && sessionInfoList !=null && (oracleUsername != null && oracleUsername.length() >0))
					{
						
						session.setAttribute("SESSIONBYUSER", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
					
					
					else if(operation.equalsIgnoreCase("oousersession") && sessionInfoList !=null && 
							(oracleUsername != null && oracleUsername.length() >0) && (osUser != null && osUser.length() >0))
					{
						
						session.setAttribute("SESSIONBYOOUSER", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
				
				
					else if (operation.equalsIgnoreCase("openedcursor") && sessionInfoList !=null)
					{
						session.setAttribute("OPENEDCURSORS", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
					
					else if(operation.equalsIgnoreCase("showallparam") && sessionInfoList !=null)
					{
						session.setAttribute("ALLPARAMETERS", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
					else if(operation.equalsIgnoreCase("showparam") && sessionInfoList !=null)
					{
						session.setAttribute("ONEPARAMETER", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
					else if(operation.equalsIgnoreCase("ctlfile") && sessionInfoList !=null)
					{
						session.setAttribute("CTLFILE", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
					else if(operation.equalsIgnoreCase("logfile") && sessionInfoList !=null)
					{
						session.setAttribute("LOGFILE", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
					else if(operation.equalsIgnoreCase("rollbackseg") && sessionInfoList !=null)
					{
						session.setAttribute("ROLLBACKSEGMENT", sessionInfoList);
						
						RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
						dispatcher.forward(request, response);
					}
					
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		 if(request.getParameter("op").equalsIgnoreCase("usersession") && (request.getParameter("oracleUserName") == null || request.getParameter("oracleUserName").length() == 0))

		{
			// if no oracle user specified, return to session page
			RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp?error=2");
			dispatcher.forward(request, response);
		}

		
		
		else if (request.getParameter("op").equalsIgnoreCase("oousersession") && ((request.getParameter("oracleUserName") == null || request.getParameter("oracleUserName").length() == 0)
				&& (request.getParameter("osUserName") == null || request.getParameter("osUserName").length() == 0)))
		{
			// if no oracle user specified, return to session page
			RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp?error=2");
			dispatcher.forward(request, response);
		}

		else if (request.getParameter("op").equalsIgnoreCase("ksession") && ((request.getParameter("sid") == null || request.getParameter("sid").length() == 0)
				&& (request.getParameter("serialNum") == null || request.getParameter("serialNum").length() == 0)))
		{
			// if no oracle user specified, return to session page
			RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp?error=2");
			dispatcher.forward(request, response);
		}
		 if(request.getParameter("op").equalsIgnoreCase("showparam") && (request.getParameter("parameterName") == null || request.getParameter("parameterName").length() == 0))
		 {
			// if no Parameter Name specified, return to session page
				RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp?error=2");
				dispatcher.forward(request, response);
		 }
		 
		else if((request.getParameter("op").equalsIgnoreCase("usersession") || request.getParameter("op").equalsIgnoreCase("oousersession")
				|| request.getParameter("op").equalsIgnoreCase("showparam"))
				|| (request.getParameter("oracleUserName").length() > 0 && request.getParameter("osUserName").length() > 0)
				|| (request.getParameter("sid").length() >0 && request.getParameter("serialNum").length() >0)
				|| (request.getParameter("parameterName").length() >0))
		{
			doGet(request, response);
		}
		
			
		
	
	}

}
