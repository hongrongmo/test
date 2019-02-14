package org.ei;

import java.io.File;
import java.nio.file.Files;
import java.io.IOException;
import java.io.PrintWriter;










import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.jsp.PageContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Map;

import jdk.nashorn.internal.runtime.Context;

/**
 * Servlet implementation class Reports
 */
public class Reports_BKUP01082016 extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
	
	
    public Reports_BKUP01082016() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		HttpSession session = request.getSession(false);
		
		int yearindex = Integer.parseInt(request.getParameter("year").toString());
		int year=2015 + yearindex;
		
		// reset weeknumber & weekindex 
		if(year ==2015)
		{
			session.setAttribute("weeknum", "201519");
			session.setAttribute("weekindex", "18");
		}
		else if(year ==2016)
		{
			session.setAttribute("weeknum", "201601");
			session.setAttribute("weekindex", "0");
		}
		
		
		
		 if (request.getParameter("button1") != null) {
			 response.getWriter().println("yes here i am in the servlet doGet Button1" + session.getAttribute("weeknum"));
		 }
		 
		 session.setAttribute("yearindex", yearindex);
		 session.setAttribute("year", year);
		 RequestDispatcher dispatcher = request.getRequestDispatcher("report.jsp");
			dispatcher.forward(request, response);
			
		 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		response.setContentType("text/html");
		HttpSession session = request.getSession(false);
		
		int weeknum = Integer.parseInt(request.getParameter("weeknummber").trim());
		String selectedIindex = request.getParameter("weeknummber").trim().substring(4);
		int weekindex = 0;
		
		// Set Default year
		int year;
		if(session.getAttribute("year") ==null)
		{
			year = 2016;
		}
		else
		{
			year = Integer.parseInt(session.getAttribute("year").toString());
		}
				
		
		
		
		//ArrayList<ArrayList<String>> report;
		ArrayList<Map<String,String>> report = null;
		Map<String,Integer> total = null;
		
		if(selectedIindex !=null && selectedIindex.length() >0 && selectedIindex.indexOf("0")==0)
		{
			weekindex = Integer.parseInt(selectedIindex.substring(1))-1;
		}
		else if(selectedIindex !=null && selectedIindex.length() >0)
		{
			weekindex = Integer.parseInt(selectedIindex)-1;
		}
		
		//PrintWriter out = response.getWriter();
		
		//out.println("WeekNumber is : " + weeknum);
		
		
		
		try
		{
			if(session !=null && session.getAttribute("username") !=null)   // should be the one when deploy
			//if(session !=null)   // for testing local only
			{
				if(request.getParameter("submit") !=null)
				{
					Report rp = new Report(weeknum);
					report = rp.getReport();
					total = rp.getTotalCount();
				
				
					session.setAttribute("weeknum", weeknum);
					session.setAttribute("weekindex", weekindex);
					session.setAttribute("Report", report);
					session.setAttribute("Total", total);
					
					// Set weekNumber Date Range
					
					Calendar cal2 = new GregorianCalendar();
					Date trial = new Date();
					cal2.setTime(trial);
					
					int yearweek = cal2.get(Calendar.WEEK_OF_YEAR) +1;
					int diff = yearweek - (weekindex+1);
					
					
					Calendar cal = Calendar.getInstance();
					int i = cal.get(Calendar.DAY_OF_WEEK) - cal.getFirstDayOfWeek();
					if(year ==2015)
					{
						//cal.add(Calendar.DATE, -(diff*7)-i+1);  //original that was working for 2015, where initially year weeks up to 52
						cal.add(Calendar.DATE, -(diff*7)-i+3);  //  [12/28/2015] bc year last loadnumber in 2015 was 53 not 52
					}
					
					// [12/28/2015] added bc last loadnumber was 201553 not 201552
					else if (year >=2016)
					{
						cal.add(Calendar.DATE, -(diff*7)-i+1);
					}
					Date start = cal.getTime();
					
					cal.add(Calendar.DATE,  4);
					
					Date end = cal.getTime();

					 session.setAttribute("WeekRange", start.toString().substring(0, 11).trim() + " - " + end.toString().substring(0, 11).trim());
					 
					 
				    
					RequestDispatcher dispatcher = request.getRequestDispatcher("report.jsp");
					dispatcher.forward(request, response);
					
					
				}
				else if(request.getParameter("export") !=null)
				{
					String filename= getServletContext().getRealPath("Report.pdf");
						
					//Export to pdf file
					new Export().createPdf(filename,(ArrayList<Map<String,String>>)session.getAttribute("Report"),(Map<String, Integer>)session.getAttribute("Total"),(String)session.getAttribute("WeekRange"));
					
					//Download Pdf file 
						File file = new File(filename);
						if (file.exists())
						{
							  response.setHeader("Content-Type", getServletContext().getMimeType(file.getName()));
						        response.setHeader("Content-Length", String.valueOf(file.length()));
						        //response.setHeader("Content-Disposition", "inline; filename=\"newFileName.pdf\"");   // open pdf direct without prompt to open
						        response.setHeader("Content-Disposition", "attachment; filename=\"DataLoading Report.pdf\"");  // open pdf through user prompt to open/cancel
						        Files.copy(file.toPath(), response.getOutputStream());
						}
				}
				
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	
}
