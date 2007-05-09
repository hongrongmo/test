package org.ei.controller;


import java.io.*;
import java.text.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;

import org.ei.controller.content.*;
import org.ei.controller.logging.*;
import org.ei.session.*;
import org.ei.util.*;


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

