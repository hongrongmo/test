<%@page import="org.ei.config.EVProperties"%>
<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ page import="java.text.NumberFormat"%>
<%@ page import="java.net.InetAddress"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Amazon Web Service Info</title>
<style>
	table{
		margin:0;
		padding:0;

	}

	table td{
		border:1px solid black;
		padding-right:5px;
		padding-left:5px;
		text-align:right;

	}

	.tLeft{text-align:left;}

	#keys, #vals{float:left;}
	#keys{text-align:right;}
	#vals{padding-left:5px;}
</style>
</head>
<body>
<H1>ENGVILLAGE DATA SERVICE</H1>
<PRE  style="border: 1px solid black; padding: 10px">
	<%
		out.write("<br>  HOST: " + (InetAddress.getLocalHost()).getHostName());
		out.write("<br>SERVER: " + getServletContext().getServerInfo());
		out.write("<br>    OS: " + System.getProperty("os.name") + ", " + System.getProperty("os.version"));
		out.write("<br>    VM: " + System.getProperty("java.vendor") + ", " + System.getProperty("java.version"));
		out.write("<br>    VM: " + System.getProperty("java.vm.name") + ", " + System.getProperty("java.vm.vendor") + ", " + System.getProperty("java.vm.version"));
        out.write("<br>  JAVA: " + System.getProperty("java.specification.name") + ", " + System.getProperty("java.specification.vendor") + ", " + System.getProperty("java.specification.version"));
        out.write("<br>   VER: " + EVProperties.getProperty("release.version"));
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
	*/
	%>
</PRE>

<h1>Amazon Instance Information</h1>
<PRE  style="border: 1px solid black; padding: 10px">
INSTANCE ID: ${actionBean.awsInfo.ec2Id}<br>     AMI ID: ${actionBean.awsInfo.amiId}<br>  Host Name: ${actionBean.awsInfo.hostName}
</PRE>

<c:if test="${usersession ne null}">
<h1>User Session Information</h1>
<PRE  style="border: 1px solid black; padding: 10px">
USER INFORMATION
=========================================================================================
<c:if test="${null != actionBean.context.existingSession}">Session ID: ${actionBean.context.existingSession.id}</c:if>
First name: ${usersession.user.firstName}
Last name: ${usersession.user.lastName}
Username: ${usersession.user.username}
Email: ${usersession.user.email}
Webuserid: ${usersession.user.webUserId}
Profile ID: ${usersession.user.profileId}

Access: ${usersession.user.userAccess}
Allowed Reg Type: ${usersession.user.allowedRegType}
Cred type: ${usersession.user.credType}
Anonymity: ${usersession.user.userAnonymity}

IP Address: ${usersession.user.ipAddress}
Start Page: ${usersession.user.startPage}
Default DB: ${usersession.user.defaultDB}
Customer ID: ${usersession.user.customerID}
Cartridge: ${usersession.user.cartridgeString}

ACCOUNT INFORMATION
=========================================================================================
Account Name: ${usersession.user.account.accountName}
Account Number: ${usersession.user.account.accountNumber}
Account ID: ${usersession.user.account.accountId}

Dept name: ${usersession.user.account.departmentName}
Dept ID: ${usersession.user.account.departmentId}
</PRE>
</c:if>

</body>
</html>