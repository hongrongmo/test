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
 * Servlet implementation class DbTables
 */
public class DbTables extends HttpServlet {
	private static final long serialVersionUID = 1L;

	String url = "";
	String dbPassword = "";
	
	HashMap<String, String> schemaTable = new HashMap<String, String>();

    public DbTables() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get the list of tables for all schema of the specified RDS 
		response.setContentType("text/html");
		
		HttpSession session = request.getSession(false);
		PrintWriter out = response.getWriter();
		
		if(session !=null && request.getParameter("database") !=null)
		{
			int rdsid = Integer.parseInt(request.getParameter("database").toString());
			
			DataStructureInfo dsInfo = new DataStructureInfo();
			/*url = dsInfo.mapUrl(rdsid);
			dbPassword = dsInfo.mapPassword(rdsid);*/
			
			url = RdsMapping.mapUrl(rdsid);
			dbPassword = RdsMapping.mapPassword(rdsid);
			// need DBA username/pwd to be able to run the sql stetements, get the schema tables list only once
				SchemaTableInfo schmTblInfo = new SchemaTableInfo(url, dbPassword);
			
				schemaTable =  schmTblInfo.getSchemaTables();

		 	if(schemaTable !=null)
		 	{
		 		session.setAttribute("SCHEMATABLES", schemaTable);
		 	}
		 	
		 	
		 	RequestDispatcher dispatcher = request.getRequestDispatcher("dbschema.jsp");
			dispatcher.forward(request, response);
		}
		
		
		
	}
	
	

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
