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
 * Servlet implementation class login
 */
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException 
	{
		// TODO Auto-generated method stub
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
				
				
		String userName = request.getParameter("username");
		String password = request.getParameter("password");
		
		HttpSession session = request.getSession(false);
		
		RequestDispatcher dispatcher = null;
		
		if(session !=null)
		{
			session.setAttribute("username", userName);
			
			LoginValidator v1 = new LoginValidator();
			try {
				if(v1.validate(userName,password))
				{
					session.setAttribute("Valid", "valid");
					session.setAttribute("ROLE", v1.getUserRole());
					
					//set RDS endpoints for later use in dbaoperations
					
					session.setAttribute("EIAENDPOINT", "eia.cmdvszxph9cf.us-east-1.rds.amazonaws.com");
					session.setAttribute("EIBENDPOINT", "eib.cmdvszxph9cf.us-east-1.rds.amazonaws.com");
					session.setAttribute("EIDENDPOINT", "eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com");
					
					if(session.getAttribute("ROLE") !=null && session.getAttribute("ROLE").toString().equalsIgnoreCase("daytondba"))
					{
						dispatcher = request.getRequestDispatcher("daytondbaoperations.jsp");
					}
					
					else
					{
						dispatcher = request.getRequestDispatcher("home.jsp");
					}
					
					dispatcher.forward(request, response);
				}
				
				else
				{ 	
					session.setAttribute("Valid", "invalid");
					dispatcher = request.getRequestDispatcher("login.jsp?error=1");
					dispatcher.forward(request, response);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
