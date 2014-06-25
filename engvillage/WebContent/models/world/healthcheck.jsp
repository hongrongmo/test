<%@ page contentType="text/html;charset=UTF-8" session="false" language="java" %>

<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.net.InetAddress"%>

<html>
<body>
<p>ENGVILLAGE DATA SERVICE</p>
<div style="border: 1px solid black; padding: 0 5px">
<%
out.write("<PRE>");
out.write("<br>  HOST: " + (InetAddress.getLocalHost()).getHostName());
out.write("<br>SERVER: " + getServletContext().getServerInfo());
out.write("<br>    OS: " + System.getProperty("os.name") + ", " + System.getProperty("os.version"));
out.write("<br>    VM: " + System.getProperty("java.vendor") + ", " + System.getProperty("java.version"));
out.write("<br>    VM: " + System.getProperty("java.vm.name") + ", " + System.getProperty("java.vm.vendor") + ", " + System.getProperty("java.vm.version"));
out.write("<br>  JAVA: " + System.getProperty("java.specification.name") + ", " + System.getProperty("java.specification.vendor") + ", " + System.getProperty("java.specification.version"));
out.write("<br>");
out.write("</PRE>");

out.write("<PRE>");
out.write("<br>        Auth IP address = " + request.getAttribute("ipaddress"));
out.write("<br>      Remote IP address = " + request.getRemoteAddr());
out.write("<br>x-forwarded-for address = " + request.getHeader("x-forwarded-for"));
out.write("<br>                 Locale = " + request.getLocale() );
out.write("<br>");
out.write("</PRE>");

        Runtime rt = Runtime.getRuntime();
        long totalMemory = rt.totalMemory();
        long freeMemory = rt.freeMemory();

    out.write("<PRE>");
    out.write("<br>TOTAL MEMORY: "+ NumberFormat.getInstance().format(totalMemory));
    out.write("<br> FREE MEMORY: "+ NumberFormat.getInstance().format(freeMemory));
    out.write("</PRE>");
/*
    long startup = ((Long) getServletContext().getAttribute("starttime")).longValue();
    long currentTime = System.currentTimeMillis();
    long uptime = currentTime - startup;
    double upsecs  = uptime/1000;
    double upmins  = upsecs/60;
    double uphours = upmins/60;
    double updays = uphours/24;
    out.write("<PRE>");
    out.write("<br>      UPTIME: ");
    out.write("<br>        DAYS: " + NumberFormat.getInstance().format((int)(updays)));
    out.write("<br>       HOURS: " + NumberFormat.getInstance().format((int)(uphours % 24)));
    out.write("<br>         MIN: " + NumberFormat.getInstance().format((int)(upmins % 60)));
    out.write("<br>     SECONDS: " + NumberFormat.getInstance().format((int)(upsecs % 60)));
    out.write("</PRE>");
*/
%>
</div>
</body>
</html>
