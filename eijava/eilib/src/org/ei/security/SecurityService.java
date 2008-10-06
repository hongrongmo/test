package org.ei.security;

import java.io.Writer;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SecurityService
	extends HttpServlet
{


    public void doPost(HttpServletRequest request,
                       HttpServletResponse response) throws ServletException
    {
        doGet(request, response);
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response) throws ServletException
    {
		try
		{
			response.setContentType("text/html");
        	Writer out = response.getWriter();
			out.write("<html><head><title>Shame on you</title></head><body>You are a bad person.<body>");
    		out.close();
		}
		catch(Exception e)
		{
			log("Security exception:", e);
		}
    }
}
