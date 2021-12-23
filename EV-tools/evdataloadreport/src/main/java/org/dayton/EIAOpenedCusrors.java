package org.dayton;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class EIAOpenedCusrors
 */
public class EIAOpenedCusrors extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public EIAOpenedCusrors() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		String infoType = "";
		int sid = 0;
		String dynamicQuery = "";
		
		ArrayList <Map<String,String>> cursorInfo = new ArrayList<Map<String,String>>();
		LinkedList<String>  columnNames = new LinkedList<String>();
		
		EIAOpenedCursorsInfo detailedCursorInfo = null;
		
		
		if(session !=null && request.getParameter("op") !=null)
		{	
			
			/** Clear sessions INFO first ****/
			
			if(session.getAttribute("DETAILEDCURSORINFO") !=null)
			{
				session.removeAttribute("DETAILEDCURSORINFO");
				session.removeAttribute("COLUMNNAMES");
			}
			
			
			
			infoType = request.getParameter("op");
			session.setAttribute("CURSORINFOTYPE", infoType);
			
			if(request.getParameter("sid") !=null && !(request.getParameter("sid").isEmpty()))
			{
				sid = Integer.parseInt(request.getParameter("sid"));
				detailedCursorInfo = new EIAOpenedCursorsInfo(infoType,sid);
			}
			/*else if(request.getParameter("dynamicsqlquery") !=null)
			{
				dynamicQuery = request.getParameter("dynamicsqlquery").toString().trim();
				dynamicQuery = dynamicQuery.replace(";", "");
				detailedCursorInfo = new EIAOpenedCursorsInfo(infoType,dynamicQuery);
			}*/
			else
			{
				detailedCursorInfo = new EIAOpenedCursorsInfo(infoType);
			}
				cursorInfo = detailedCursorInfo.getCursorInfo();
				columnNames = detailedCursorInfo.getColumnNames();
			
			if(cursorInfo.size() >0)
			{
				session.setAttribute("DETAILEDCURSORINFO", cursorInfo);
				session.setAttribute("COLUMNNAMES", columnNames);
			}
		}
		
		
			RequestDispatcher dispatcher = request.getRequestDispatcher("daytondbaoperations.jsp");
			dispatcher.forward(request, response);
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		doGet(request, response);
	}

}
