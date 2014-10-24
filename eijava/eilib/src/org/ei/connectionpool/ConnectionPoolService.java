package org.ei.connectionpool;


import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.ei.util.StringUtil;
import org.ei.domain.ClassNodeManager;



public class ConnectionPoolService extends HttpServlet
{
    private StringUtil sUtil = new StringUtil();
    private String poolConf;

    public void init()
        throws ServletException
    {

        ServletContext context = getServletContext();

        try
        {

            poolConf = context.getRealPath("/WEB-INF/pools.xml");
            System.out.println("Starting connectionpool:"+poolConf);
            ConnectionBroker.getInstance(poolConf);


        }
        catch(Exception e)
        {
            log("Init Error:", e);
            throw new ServletException(e.getMessage(), e);
        }
    }

    public void destroy()
    {
        try
        {
            System.out.println("Stopping connectionpool:"+poolConf);
            ConnectionBroker broker = ConnectionBroker.getInstance(poolConf);
            broker.closeConnections();
            System.out.println("Closing ClassNode Manager:");
            ClassNodeManager.getInstance().close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public void service(HttpServletRequest request, HttpServletResponse response)
        throws IOException, ServletException
    {



    }


}
