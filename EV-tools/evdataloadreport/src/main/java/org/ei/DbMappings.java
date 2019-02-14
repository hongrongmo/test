package org.ei;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DbMappings
 */
public class DbMappings extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	String url = "";
	String dbPassword = "";
	
	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DbMappings() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		
		PrintWriter out = response.getWriter();
		
		if(session !=null && request.getParameter("database") !=null)
			{
				
				int rdsid = Integer.parseInt(request.getParameter("database").toString());
				session.setAttribute("database", rdsid);
				
				DataStructureInfo dsInfo = new DataStructureInfo();
				/*url = dsInfo.mapUrl(rdsid);
				dbPassword = dsInfo.mapPassword(rdsid);*/
				
				url = RdsMapping.mapUrl(rdsid);
				dbPassword = RdsMapping.mapPassword(rdsid);
				
				// add url & pwd in session for later use in Session Servlet
				
				if(url !=null)
				{
					session.setAttribute("url", url);
				}
				if(dbPassword !=null)
				{
					session.setAttribute("dbpwd", dbPassword);
				}
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("sessions.jsp");
				dispatcher.forward(request, response);

			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		
		}
		

}
