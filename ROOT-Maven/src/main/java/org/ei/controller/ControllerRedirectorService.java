package org.ei.controller;


import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ControllerRedirectorService
    extends HttpServlet
{

    public void service(HttpServletRequest request,
                HttpServletResponse response)
        throws IOException, ServletException
    {
        String serverName = request.getServerName();
        String uri = request.getRequestURI();
        if(uri != null && uri.equals("/robots.txt"))
        {
            response.setContentType("text/plain");
            PrintWriter out = response.getWriter();
            out.println("User-agent: *");
            out.println("Disallow: /");
            out.flush();
            out.close();
        }
        else
        {
            int serverPort = request.getServerPort();
            if(serverPort != 80)
            {
                serverName = serverName+":"+Integer.toString(serverPort);
            }
            response.sendRedirect("http://"+serverName+"/controller/servlet/Controller");
        }
    }

}

