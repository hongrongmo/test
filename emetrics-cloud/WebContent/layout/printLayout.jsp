<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<html:html>
  <head>
    <title><tiles:getAsString name="title"/></title>
	<SCRIPT LANGUAGE="JavaScript" SRC='<html:rewrite page="/js/stylesheet.js"/>'>
	</SCRIPT>
  </head>
<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<html:link href="javascript:this.window.close();" styleClass="LgBlueText">Close Window</html:link>

<bean:define toScope="request" id="printerfriendly" value="true"/>

<table border="0" width="100%" cellspacing="0">
<tr>
  <td valign="top"  align="left">
	<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr>
		<td valign="top">
			<tiles:insert attribute="body" />
		</td>
	</tr>
	</table>
  </td>
</tr>
<tr>
  <td colspan="2">
    <tiles:insert attribute="footer" />
  </td>
</tr>
</table>
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
var pageTracker = _gat._getTracker("UA-6113832-2");
pageTracker._trackPageview();
</script>
</body>
</html:html>

