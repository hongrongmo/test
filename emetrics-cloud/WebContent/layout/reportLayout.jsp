<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>
<%@ taglib uri="/WEB-INF/struts-tiles.tld" prefix="tiles" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ page import="org.ei.struts.emetrics.Constants"%>
<html:html>
  <head>
    <title><tiles:getAsString name="title"/></title>
    <SCRIPT LANGUAGE="JavaScript" SRC='<html:rewrite page="/js/stylesheet.js"/>'>
    </SCRIPT>
    <SCRIPT LANGUAGE="JavaScript" SRC='<html:rewrite page="/js/form.js"/>'></SCRIPT>
  </head>
<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
  <table border="0" width="100%" cellspacing="0" cellpadding="0">
  <tr>
    <td colspan="2">
      <tiles:insert attribute="header" />
    </td>
  </tr>
  <tr valign="top">
    <td colspan="2">
      <table border="0" width="100%" cellspacing="0" cellpadding="0">
          <tr valign="top">
              <td valign="top" width="190">
                  <tiles:insert attribute="menu" />
              </td>
              <td valign="top" width="6">
                  <html:img pageKey="images.spacer" border="0" width="6"/>
              </td>
              <td valign="top">
  								<bean:page id="thisreq" property="request"/>
                  <logic:equal name="reportForm" property="action" value="<%= Constants.ACTION_DISPLAY_REPORT %>">
                    <html:link name="thisreq" property="parameterMap" forward="print" target="_new" styleClass="LgBlueText" ><bean:message key="label.link.printer"/></html:link>
                    <br/>
                  </logic:equal>
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
  <script language="JavaScript" type="text/javascript" src='<html:rewrite page="/js/wz_tooltip.js"/>'></script>
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

