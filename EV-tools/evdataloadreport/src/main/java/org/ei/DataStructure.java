package org.ei;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.ei.DataStructureInfo;

/**
 * Servlet implementation class DataStructure
 */
public class DataStructure extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataStructure() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		//Map<String,String> tableStructure = new HashMap <String,String>();
		ArrayList <Map<String,String>> tableStructure = new ArrayList<Map<String,String>>();
		ArrayList <Map<String,String>> tableIndexes = new ArrayList<Map<String,String>>();
		ArrayList <Map<String,String>> tableConstraints = new ArrayList<Map<String,String>>();
		
		String schemaName = null;
		String tableName = null;
		//get the table name from parameter
		
		
		
		try
		{


			if(session !=null && session.getAttribute("database") !=null)
			{
				int rdsid = Integer.parseInt(session.getAttribute("database").toString());

				/*if(request.getParameter("table") !=null)*/   //original for switch case
				
				if(request.getParameter("s") !=null && request.getParameter("t") !=null)
				{
					session.setAttribute("SCHEMA", request.getParameter("s"));
					session.setAttribute("TABLENAME", request.getParameter("t"));


					/*switch(Integer.parseInt(request.getParameter("table").toString()))
					{
					case 1:
						session.setAttribute("TABLENAME", "bd_master");
						session.setAttribute("SCHEMA", "db_xml");
						break;
					case 2: 
						session.setAttribute("TABLENAME", "c84_master");
						session.setAttribute("SCHEMA", "db_xml");
						break;
					case 3:
						session.setAttribute("TABLENAME", "ins_2014_master");
						session.setAttribute("SCHEMA", "db_inspec");
						break;
					case 4: 
						session.setAttribute("TABLENAME","upt_master");
						session.setAttribute("SCHEMA", "db_patent");
						break;
					case 5:
						session.setAttribute("TABLENAME", "ept_master");
						session.setAttribute("SCHEMA", "db_encompass");
						break;
					case 6: 
						session.setAttribute("TABLENAME", "cbn_master");
						session.setAttribute("SCHEMA", "db_cbnb");
						break;
					case 7:
						session.setAttribute("TABLENAME", "georef_master_2014");
						session.setAttribute("SCHEMA", "db_georef");
						break;
					}*/

					DataStructureInfo dstructure= new DataStructureInfo(rdsid,session.getAttribute("SCHEMA").toString(),session.getAttribute("TABLENAME").toString());
					tableStructure = dstructure.getTableStructure();
					tableIndexes = dstructure.getTableindexes();
					tableConstraints = dstructure.getTableConstraints();
					
					
					
					if(tableStructure !=null)
					{
						session.setAttribute("STRUCTURE",tableStructure);
					}
					if(tableIndexes !=null)
					{
						session.setAttribute("INDEXES", tableIndexes);
					}
					if(tableConstraints !=null)
					{
						session.setAttribute("CONSTRAINTS", tableConstraints);
					}
					
					RequestDispatcher dispatcher = request.getRequestDispatcher("tablestructure.jsp");
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
		// TODO Auto-generated method stub
	}

}
