package org.ei;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Tablespace
 */
public class Tablespace extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Tablespace() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		
		ArrayList<Map<String,String>> ts = null;
		ArrayList<Map<String,String>> ts2 = null;
		String totalSum = null;
		
		int rdsid = 0;
		
		
		
		try
		{
			if(session !=null)
			{	
				if(request.getParameter("ts") !=null)
				{
					rdsid = Integer.parseInt(request.getParameter("ts"));
					
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
				}
				
				/*if(session.getAttribute("TS") == null || session.getAttribute("TS2") == null)
				{*/
					TableSpaceInfo ts_info= new TableSpaceInfo(rdsid);
					ts = ts_info.getTableSpace();
					ts2 = ts_info.getTableSpace2();
					totalSum = ts_info.getTableSpace3();
				/*}*/
				if(ts !=null)
				{
					session.setAttribute("TS", ts);
				}
				
				if(ts2 !=null)
				{
					session.setAttribute("TS2", ts2);
				}
				
				if(totalSum !=null)
				{
					session.setAttribute("TS3", totalSum);
				}
				
				
				RequestDispatcher dispatcher = request.getRequestDispatcher("tablespace.jsp");
				dispatcher.forward(request, response);
				
				
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
		
		
	}

}
