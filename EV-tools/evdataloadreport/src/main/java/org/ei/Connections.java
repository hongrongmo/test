package org.ei;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Connections
 */
public class Connections extends HttpServlet {
	private static final long serialVersionUID = 1L;
      
	String url = "";
	String dbPassword = "";
	
	ArrayList <Map<String,String>> dbConnectionsByUserList = new ArrayList<Map<String,String>>();
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Connections() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);

		if(session !=null && (request.getParameter("database") !=null))
		{

				int rdsid = Integer.parseInt(request.getParameter("database").toString());
				session.setAttribute("database", rdsid);

				// to display RDS Name in dbconnections.jsp
				if(rdsid == 1)
				{
					session.setAttribute("RDSNAME", "EIA");
				}
				else if (rdsid ==2)
				{
					session.setAttribute("RDSNAME", "EIB");
				}
				else if (rdsid ==3)
				{
					session.setAttribute("RDSNAME", "EID");
				}



				// to get rds url & rds pwd
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

				// get dbconnections total count
				DbConnectionsInfo conInfo = new DbConnectionsInfo(url, dbPassword);
				int totalCon = conInfo.fetchTotalDbConnections();
				
				if(totalCon > 0)
				{
					session.setAttribute("TOTALDBCONNECTIONS", totalCon);
				}
				// get dbconnections total count by username
				dbConnectionsByUserList = conInfo.getConnectionCountByUsername();
				
				if(dbConnectionsByUserList.size() >0)
				{
					session.setAttribute("CONNECTIONCOUNTBYUSER", dbConnectionsByUserList);
				}


			RequestDispatcher dispatcher = request.getRequestDispatcher("dbconnections.jsp");
			dispatcher.forward(request, response);



		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
