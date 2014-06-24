package org.ei.system;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.EVProperties;
import org.ei.config.RuntimeProperties;
import org.ei.session.EVSessionListener;

public class HeartbeatService extends HttpServlet {

    private static final Logger log4j = Logger.getLogger(HeartbeatService.class);

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private long startup;

    public void init() throws ServletException {
        this.startup = System.currentTimeMillis();

    }

    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        String releaseversion = RuntimeProperties.getInstance().getProperty(RuntimeProperties.RELEASE_VERSION);
        if (GenericValidator.isBlankOrNull(releaseversion)) {
            releaseversion = "UNKNOWN!";
        }


        response.setContentType("text/html");
        Runtime rt = Runtime.getRuntime();
        double totalMemory = rt.totalMemory()/1024/1024;
        double freeMemory = rt.freeMemory()/1024/1024;
        log4j.warn("Heartbeat check, total memory=" + totalMemory + " MB, free memory=" + freeMemory + " MB, active sessions=" + EVSessionListener.getActiveSessions() + ", releaseversion=" + releaseversion);

        PrintWriter out = response.getWriter();
        long currentTime = System.currentTimeMillis();
        long uptime = currentTime - startup;
        double uphours = (((uptime / 1000) / 60) / 60);
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(currentTime);

        out.println("<html>");
        // Head
        out.println("<head>");
        out.println("<title>HeartbeatService</title>");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"/static/css/ev_common_sciverse.css?d=" + releaseversion + "\" />");
        out.println("<link rel=\"stylesheet\" type=\"text/css\" media=\"screen\" href=\"/static/css/ev_txt.css?d=" + releaseversion + "\" />");
        out.println("<link type=\"image/x-icon\" href=\"/static/images/engineering_village_favicon.gif\" rel=\"SHORTCUT ICON\"/>");
        out.println("</head>");
        out.println("<body class='padding5'>");
        // Contents
        out.println("<h1>Engineering Village Heartbeat Service</h1>");
        out.println("<p>RELEASE VERSION: " + releaseversion + "</p>");
        out.println("<p>UPTIME: " + Double.toString(uphours) + " hours</p>");
        out.println("<p>TOTAL MEMORY: " + totalMemory + " MB</p>");
        out.println("<p>FREE MEMORY: " + freeMemory + " MB</p>");
        out.println("<p>ACTIVE SESSIONS: " + EVSessionListener.getActiveSessions() + "</p>");
        out.println("<br/><p class='txtSmallItalic'>Copyright &copy; " + cal.get(Calendar.YEAR) + " by Engineering Information Inc., Hoboken, New Jersey, U.S.A.</p>");
        out.println("</body>");
        out.println("</html>");
    }
}
