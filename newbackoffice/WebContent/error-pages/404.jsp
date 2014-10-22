<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-template.tld" prefix="template" %>

<html:html>
<head>
  <title>File not found</title>
  <script language="Javascript1.1" src='<html:rewrite page="/js/stylesheet.jsp"/>'></script>
</head>

<body topmargin="0" marginheight="0" marginwidth="0" bgcolor="#FFFFFF">

<jsp:useBean id="Constants" class="org.ei.struts.backoffice.Constants" scope="application"/>

<html:errors/>

<!-- top logo area-->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" height="45" bgcolor="#CCCCCC">
			<html:img height="45" border="0" pageKey="images.spacer"/>
		</td>
	</tr>
</table>
</center>
<!-- End of top logo area -->

<!-- Start of body portion -->
<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top" width="190" bgcolor="#CCCCCC">

		<template:insert template='/templates/nav.jsp'>
			<template:put name='links' content='/templates/_links.jsp'/>
		</template:insert>

		</td>
		<td valign="top" width="10">
			<html:img width="10" border="0" pageKey="images.spacer"/>
		</td>
		<td valign="top">
		<!-- Start of table to put the form -->

		<bean:define toScope="request" id="Border" value="#BCBD78"/>
		<bean:define toScope="request" id="Inner" value="#EFEFD6"/>
		&nbsp;
		<ul>
		<li>File not found.</li>
		</ul>

	  </td>
	</tr>
  </table>
</center>
<!-- End of body portion -->

</body>
</html:html>
