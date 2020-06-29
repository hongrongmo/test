package org.ei;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DAnalysis
 */
public class DAnalysis extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	String category = null;
	String url = null;
	//String url = "jdbc:oracle:thin:@localhost:15212:eid";   // for local testing
	//String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";   // for deployment
	String driver = "oracle.jdbc.driver.OracleDriver";
	//String dbUserName = "ap_correction1";
	String dbUserName = "ap_report";
	String dbPassword = "";
	

	Connection con;
	Statement stmt =null;
	ResultSet rs = null;
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DAnalysis() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// to add a new issue 
		HttpSession session = request.getSession(false);
		
		if (session !=null && request.getParameter("addnewissue") !=null)
		{
			session.setAttribute("ADDNEWDATAISSUE", "true");
			RequestDispatcher dispatcher = request.getRequestDispatcher("addataissue.jsp");
			dispatcher.forward(request, response);
		}
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		
		PrintWriter out = response.getWriter();
		
		ArrayList<String> issues_Title_List = new ArrayList<String>();
		
		if(session !=null && request.getParameter("issuecategory") !=null)
		{
			/* first remove "DATAISSUESINFO" from session if any data issue has been viewed/updated before so it does not show up automatically when go to 
			 other category or even to same category 
			 */
			
			session.removeAttribute("DATAISSUESINFO");
			
			/*issues_ID = Integer.parseInt(request.getParameter("id").toString());*/
			category = request.getParameter("issuecategory").toString();


			// set category in user session for later use in retrieving detailed info of the issue for that specified category
			session.setAttribute("DATAISSUECATEGORY", category);  
			
			
			if(category !=null)
			{
				try
				{
					String query = "select distinct title from dataload_issues where lower(category) = '"+category.toLowerCase()+"' order by title";

					url = RdsMapping.singleRdsMapping();
					
					con = getConnection(url, driver, dbUserName, dbPassword);
					stmt = con.createStatement();
					rs = stmt.executeQuery(query);


					while(rs.next())
					{
						issues_Title_List.add(rs.getString(1));
					}

					if(issues_Title_List.size() >0)
					{
						session.setAttribute("ISSUESTITLELIST", issues_Title_List);
					}
				}
				catch(SQLException sqlex)
				{
					System.out.println(sqlex.getMessage());
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}

				finally
				{
					if(con !=null)
					{
						try
						{
							con.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					if(stmt !=null)
					{
						try
						{
							stmt.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}

					if(rs !=null)
					{
						try
						{
							rs.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}

			}

			RequestDispatcher dispatcher = request.getRequestDispatcher("dataissues.jsp");
			dispatcher.forward(request, response);
		}
		
		else
		{
			RequestDispatcher dispatcher = request.getRequestDispatcher("dataanalysis.jsp");
			dispatcher.forward(request, response);
		}

	}
	
	public Connection getConnection(String connectionURL, String driver, String username, String password)	
			throws Exception
			{
				Class.forName(driver);
				con = DriverManager.getConnection(connectionURL, username, password);
				return con;
			}
	

}
