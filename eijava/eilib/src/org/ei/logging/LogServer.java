package org.ei.logging;

import java.io.IOException;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;





public class LogServer
    extends HttpServlet
{

    private CLFLogger logger;

    public void init()
        throws ServletException
    {
        System.out.println("Init LogServer");

        try
        {
            ServletConfig config = getServletConfig();
            String path = config.getInitParameter("logfile");
            System.out.println("Path to logfile:"+path);
            logger = new CLFLogger(path);
        }
        catch(Exception e)
        {
            throw new ServletException(e.getMessage(), e);
        }

    }

    public void destroy()
    {
        try
        {
            logger.shutdown();
        }
        catch(Exception e)
        {
            // nothing here yet
        }

    }


    public void service(HttpServletRequest request,
                        HttpServletResponse response)
             throws IOException, ServletException
    {

        ServletOutputStream out = response.getOutputStream();
        String clfMessage = messenge(request);
        logger.enqueue(new CLFMessage(clfMessage));
    }

    public String messenge(HttpServletRequest request)
    {
        String message = new String();
        message = "<" + request.getParameter("appid") + "> ";
        message = message + "<" + request.getParameter("reqid") + "> ";
        message = message + "<" + toCLF(request) + "> ";
        message = message + "<" + request.getParameter("cookies") + "> ";
        message = message + "<" + request.getParameter("appdata") + "> ";
        return message;
    }

    private String toCLF(HttpServletRequest request)
    {
        StringBuffer clf = new StringBuffer();
        Calendar calendar = new GregorianCalendar();
        //
        Date date = new Date();
        date.setTime((long) Long.parseLong(request.getParameter("date")));
        calendar.setTime(date);
        int zone = (calendar.get(Calendar.ZONE_OFFSET) / (60 * 60 * 1000));
        DateFormat format3 = new SimpleDateFormat("[dd/MMM/yyyy:HH:mm:ss ");
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMinimumIntegerDigits(2);


        clf.append(request.getParameter("host"));
        clf.append(" " + request.getParameter("rfc931"));
        clf.append(" " + request.getParameter("username"));
        clf.append(" " + format3.format(date) + nf.format(zone) + "00]");
        clf.append(" \"" + request.getParameter("HTTPmethod"));
        clf.append(" " + request.getParameter("uri_stem"));
        //|| ((request.getParameter("uri_query")).compareTo("null") == 0)
        if (request.getParameter("uri_query") != null) {
            clf.append("?" + request.getParameter("uri_query"));
        }
        clf.append(" HTTP/" + request.getParameter("prot_version"));
        clf.append("\" " + request.getParameter("statuscode"));
        clf.append(" " + request.getParameter("bytes"));
        if ( request.getParameter("referrer") != null ) {
            if ( request.getParameter("referrer").equalsIgnoreCase("null") ) {
                clf.append(" \"-");
            } else {
                clf.append(" \"" + request.getParameter("referrer"));
            }
        } else {
            clf.append(" \"-");
        }
        clf.append("\" \"" + request.getParameter("user_agent"));
        clf.append("\" \"" + request.getParameter("cookies") + "\"");

        return (clf.toString());
    }
}

