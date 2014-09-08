<%@ page  session="false"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>

<%@ page  import="java.text.NumberFormat"%>
<%@ page  import="java.net.InetAddress"%>
<%@ page  import="javax.naming.Context"%>
<%@ page  import="javax.naming.InitialContext"%>
<%@ page  import="javax.naming.NamingException"%>

<%@ page  import="javax.sql.DataSource"%>

<%@ page  import="org.apache.commons.dbcp.*"%>
<%@ page  import="org.ei.struts.emetrics.Constants"%>

<html:html>
<head>
<title><bean:message key="global.title"/></title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body>
<%

    Context initCtx = new InitialContext();
    Context envCtx = (Context) initCtx.lookup(Constants.DEFAULT_CONTEXT);

    out.write("EMETRICS");
    out.write("<PRE>");
    out.write("<br>");
    out.write("<br>    OS: " + System.getProperty("os.name") + ", " + System.getProperty("os.version"));
    out.write("<br>    VM: " + System.getProperty("java.vendor") + ", " + System.getProperty("java.version"));
    out.write("<br>SERVER: " + getServletContext().getServerInfo());
    out.write("<br>  HOST: " + (InetAddress.getLocalHost()).getHostName());
    out.write("<br>");
    out.write("</PRE>");

    out.write("<PRE>");
    out.write("<br>Your IP address = " + request.getRemoteAddr());
    out.write("<br>         Locale = " + request.getLocale() );
    out.write("<br>FORWARDED address = " + request.getHeader("x-forwarded-for"));
    out.write("</PRE>");

    BasicDataSource old = (BasicDataSource) envCtx.lookup(Constants.EMETRICS_AUTH_DBCP_POOL);
    out.write("<PRE>");
    out.write("<br>      NumActive = " + old.getNumActive());
    out.write("<br>        NumIdle = " + old.getNumIdle());
    out.write("<br>      MaxActive = " + old.getMaxActive());
    out.write("<br>DriverClassName = " + old.getDriverClassName() );
    out.write("<br>       Username = " + old.getUsername() );
    String jdbcURL = old.getUrl();
    if(jdbcURL != null)
    {
      out.write("<br>            Url = " + jdbcURL.substring(jdbcURL.lastIndexOf(".") + 1,jdbcURL.length()) );
    }
    out.write("</PRE>");


    Runtime rt = Runtime.getRuntime();
    long totalMemory = rt.totalMemory();
    long freeMemory = rt.freeMemory();

    out.write("<PRE>");
    out.write("<br>TOTAL MEMORY: "+ NumberFormat.getInstance().format(totalMemory));
    out.write("<br> FREE MEMORY: "+ NumberFormat.getInstance().format(freeMemory));
    out.write("</PRE>");

    long startup = ((Long) getServletContext().getAttribute(Constants.SERVLET_STARTTIME_KEY)).longValue();
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

%>
</body>
</html:html>
