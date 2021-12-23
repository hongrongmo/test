package org.ei;

import java.io.IOException;
import java.io.PrintWriter;
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
 * Servlet implementation class DataIssues
 */
public class DataIssues extends HttpServlet {
	private static final long serialVersionUID = 1L;
    
	HttpSession session = null;
	
	String category = null;
	String issue_Title = null;
    
	ArrayList <Map<String,String>> issuesInfoList = new ArrayList<Map<String,String>>();
	
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DataIssues() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// get the list of Issues title for display in dataissues.jsp navbar
		response.setContentType("text/html");
		session = request.getSession(false);

		
		if(session !=null && session.getAttribute("DATAISSUECATEGORY") !=null && request.getParameter("title") !=null)
		{
			category = session.getAttribute("DATAISSUECATEGORY").toString();
			issue_Title = request.getParameter("title").toLowerCase();
			
			// set title for later update statement in doPost(request,response)
			session.setAttribute("ISSUETITLE", issue_Title);
			
			
			DataIssuesInfo dinfo= new DataIssuesInfo(category,issue_Title);
			issuesInfoList = dinfo.getIssuesInfo();
			
			if(issuesInfoList !=null)
			{
				session.setAttribute("DATAISSUESINFO", issuesInfoList);
			}
			
			
			RequestDispatcher dispatcher = request.getRequestDispatcher("dataissues.jsp");
			dispatcher.forward(request, response);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		// update detailed info of the data issue
		session = request.getSession(false);
		
		Map <String,String>row = new HashMap<String,String>();
		
		if(session !=null && ((session.getAttribute("DATAISSUESINFO") !=null && request.getParameter("Update") !=null) 
				|| ( session.getAttribute("ADDNEWDATAISSUE") !=null && session.getAttribute("ADDNEWDATAISSUE").equals("true"))))
		{
			// the original issue category and title to be used in "where clause" for update
			
			if(session.getAttribute("DATAISSUECATEGORY") !=null && request.getParameter("Update") !=null)
			{
				this.category = session.getAttribute("DATAISSUECATEGORY").toString();
			}
			if(session.getAttribute("ISSUETITLE") !=null && request.getParameter("Update") !=null)
			{
				this.issue_Title = session.getAttribute("ISSUETITLE").toString();
			}
			
			
			
			if(request.getParameter("issuecategory") !=null)
			{
				row.put("CATEGORY", request.getParameter("issuecategory").toString().trim());
			}
			if(request.getParameter("txtboxtitle") !=null)
			{
				row.put("TITLE", request.getParameter("txtboxtitle").toString().trim());
			}
			if(request.getParameter("type") !=null)
			{
				row.put("TYPE", request.getParameter("type").toString().trim());
			}
			if(request.getParameter("priority") !=null)
			{
				row.put("PRIORITY", request.getParameter("priority").toString().trim());
			}
			if(request.getParameter("lables") !=null)
			{
				row.put("LABELS", request.getParameter("lables").toString().trim());
			}
			if(request.getParameter("epic_name") !=null)
			{
				row.put("EPIC_NAME", request.getParameter("epic_name").toString().trim());
			}
			if(request.getParameter("sprint") !=null)
			{
				row.put("SPRINT", request.getParameter("sprint").toString().trim());
			}
			if(request.getParameter("status") !=null)
			{
				row.put("STATUS", request.getParameter("status").toString().trim());
			}
			if(request.getParameter("resolution") !=null)
			{
				row.put("RESOLUTION", request.getParameter("resolution").toString().trim());
			}
			if(request.getParameter("assignee") !=null)
			{
				row.put("ASSIGNEE", request.getParameter("assignee").toString().trim());
			}
			if(request.getParameter("reporter") !=null)
			{
				row.put("REPORTER", request.getParameter("reporter").toString().trim());
			}
			if(request.getParameter("description") !=null)
			{
				row.put("DESCRIPTION", request.getParameter("description").toString().trim().replace("'", ""));
			}
			
			if(request.getParameter("comment") !=null)
			{
				row.put("COMMENT", request.getParameter("comment").toString().trim().replace("'", ""));
			}
			
			
			// update an exist issue
			if(session.getAttribute("DATAISSUESINFO") !=null && request.getParameter("Update") !=null)
			{
				DataIssuesInfo dinfo= new DataIssuesInfo(row,category,issue_Title);
				int sts = dinfo.updateIssue();
				
				if(sts !=-1)
				{
					System.out.println("Status of update is : " + sts);
					
					// set session attribute "DATAISSUECATEGORY" to new selected value, otherwise after update will not see details info of issue
					
					session.setAttribute("DATAISSUECATEGORY", request.getParameter("issuecategory").trim());
					
					
					// reset session attribute "DATAISSUESINFO" so next if add new issue do not need to call the update 
					session.removeAttribute("DATAISSUESINFO");
					
					RequestDispatcher dispatcher = request.getRequestDispatcher("dataissues.jsp?status="+sts);
					dispatcher.forward(request, response);
				}
				
			}
			
			// add new issue 
			else if(session.getAttribute("ADDNEWDATAISSUE").equals("true"))
			{
				DataIssuesInfo dinfo= new DataIssuesInfo(row);
				int sts = dinfo.addIssue();
				
				
				if(sts !=-1)
				{
					
					// reset session attribute "ADDNEWDATAISSUE" so next if update an exist issue do not need to call the add
					session.removeAttribute("ADDNEWDATAISSUE");
					
					RequestDispatcher dispatcher = request.getRequestDispatcher("dataanalysis.jsp?status="+sts);
					dispatcher.forward(request, response);
				}
				else
				{
					RequestDispatcher dispatcher = request.getRequestDispatcher("addataissue.jsp?status="+sts);
					dispatcher.forward(request, response);
				}
			}
			
			
			
		}
		
	}

}
