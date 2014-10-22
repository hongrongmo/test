<%@page import="java.util.Enumeration"%>
<%@page import="java.net.InetAddress"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="org.ei.stripes.util.HttpRequestUtil"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Engineering Village - IP Check</title>
</head>
<body>

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
        out.write("<br>        Auth IP address   = " + request.getAttribute("ipaddress"));
        out.write("<br>      Remote IP address   = " + request.getRemoteAddr());
        out.write("<br>x-forwarded-for address   = " + request.getHeader("x-forwarded-for"));
        out.write("<br>x-forwarded-for corrected = " + HttpRequestUtil.getIP(request));
        out.write("<br>                 Locale   = " + request.getLocale() );
        out.write("<br>");
        out.write("</PRE>");

        out.write("<PRE>");
        out.write("<br/>All Request Headers");
        Enumeration<String> en = request.getHeaderNames();
        while (en.hasMoreElements()) {
            String key = (String) en.nextElement();
            out.write("<br/>" + key + ":");
            for (int i=0; i < 30-key.length(); i++) out.write(" ");
            out.write(request.getHeader(key));
        }
        out.write("</PRE>");

%>
</body>
</html>