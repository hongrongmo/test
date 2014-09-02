<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>

<html:html locale="true">
	<head>
	<link href="<html:rewrite page="/favicon.ico"/>" rel="shortcut icon" type="image/x-icon"/>
	<title><bean:message key="logon.title"/></title>
  <script language="Javascript1.1" src="<html:rewrite page="/js/stylesheet.jsp"/>"></script>
  </head>
<body>

<!-- top logo area-->
<html:img style="margin-left:0.25in;" border="0" pageKey="images.logo"/>
<!-- End of top logo area -->

<!-- Start of body portion -->
<center>
<table border="0" cellspacing="0" cellpadding="0" width="60%">
<tr><td valign="top" colspan="3" height="20"><html:img pageKey="images.spacer" border="0" height="20"/></td></tr>
<tr><td valign="top" colspan="3"><html:img page="/images/login.gif" border="0"/></td></tr>
<tr><td valign="top" colspan="3" height="10"><html:img pageKey="images.spacer" border="0" height="10"/></td></tr>
<tr><td valign="top" height="1" bgcolor="#000000" colspan="3"><html:img pageKey="images.spacer" border="0" height="1"/></td></tr>
<tr><td valign="top" width="1" bgcolor="#000000"><html:img pageKey="images.spacer" border="0" width="1"/></td>
<td valign="top">
<!-- Start of table just for the form -->
<table border="0" cellspacing="0" cellpadding="0" width="100%" bgcolor="#EFEFD6">
<tr><td valign="top" colspan="4" height="10"><html:img page="/images//spacer.gif" border="0" height="10"/></td></tr>
<tr><td valign="top" width="3"><html:img page="/images//spacer.gif" border="0"/></td><td valign="top" colspan="3"><span Class="RedText"><html:errors/></span></td></tr>
<tr><td valign="top" colspan="4" height="10"><img src="images/spacer.gif" border="0" height="10"></td></tr>

    <html:form action="/login" focus="username" onsubmit="return validateLoginForm(this);" >

    	<tr>
    	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
    	  <td valign="middle" align="right"><span class="MedBlackText"><bean:message key="label.username"/>:</span></td>
    	  <td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10"/></td>
    	  <td valign="top"><span class="MedBlackText"><html:text property="username" size="28"/></span></td>
    	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
    	</tr>
    	<tr>
    	  <td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
    	</tr>
    	<tr>
    	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
    	  <td valign="middle" align="right"><span Class="MedBlackText"><bean:message key="label.password"/>:</span></td>
    	  <td valign="top" width="10"><html:img pageKey="images.spacer" border="0" width="10"/></td>
    	  <td valign="top"><span Class="MedBlackText"><html:password  property="password" size="28"/></span></td>
    	  <td valign="top" width="3"><html:img pageKey="images.spacer" border="0" width="3"/></td>
    	</tr>
    	<tr>
    	  <td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
    	</tr>

    	<tr>
    	  <td valign="top" colspan="3"><html:img pageKey="images.spacer" border="0"/></td>
    	  <td valign="top">
    	    <html:image pageKey="images.action.login" border="0"/>
    	    &nbsp;&nbsp;
    	    <html:image pageKey="images.cancel" onclick="javascript:reset(); return false;" border="0"/>
    	  </td>
    	</tr>
    	<tr>
    	  <td valign="top" colspan="4" height="4"><html:img pageKey="images.spacer" border="0" height="4"/></td>
    	</tr>
    </html:form>

</table>
<!-- End of table just for the form -->
</td>
<td valign="top" width="1" bgcolor="#000000"><html:img pageKey="images.spacer" border="0" width="1"/></td>
</tr>
<tr>
<td valign="top" colspan="3" height="1" bgcolor="#000000"><html:img pageKey="images.spacer" border="0" height="1"/></td>
</tr>
</table>
<!-- End of table to put the form -->
</center>
<!-- End of body portion -->

<html:javascript formName="loginForm" dynamicJavascript="true" staticJavascript="false"/>
<script language="Javascript1.1" src="staticJavascript.jsp"></script>

</body>
</html:html>
