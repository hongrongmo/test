<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="/META-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/META-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/META-INF/struts-logic.tld" prefix="logic" %>

<%@ page import="org.ei.struts.emetrics.Constants"%>

<html:html>
<head>
    <title><bean:message key="global.title"/></title>
    <SCRIPT LANGUAGE="JavaScript" SRC='<html:rewrite page="/js/stylesheet.js"/>'></SCRIPT>
</head>
<body topmargin="0" marginheight="0" marginwidth="0">

  <!-- Start of toplogo and navigation bar -->
  <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#00449E">
    <tr>
      <td valign="top"><hmtl:img src="images/spacer.gif" border="0" height="4"/></td></tr>
    <tr>
      <td valign="top">
        <html:link forward="menu"><html:img styleClass="logo" pageKey="images.logo" border="0"/></html:link>
        <span id="logoheading"><bean:message key="global.title"/></span>
      </td>
    </tr>
    <tr>
      <td>
        <html:img pageKey="images.spacer" border="0" height="20"/>
      </td>
    </tr>
    <tr>
      <td valign="top" height="1" width="690">
        <html:img pageKey="images.spacer" border="0" height="1" width="690"/>
      </td>
    </tr>
  </table>


  <!-- end of toplogo and navigation bar -->

  <!-- Start of the lower area below the navigation bar -->
  <table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr>
      <td valign="top" width="490">

        <!-- start of right side table for content -->
        <table border="0" width="490" cellspacing="0" cellpadding="0">
          <tr><td valign="top" height="15" colspan="2"><html:img pageKey="images.spacer" border="0" height="15"/></td></tr>
          <tr><td valign="top" width="4"><html:img pageKey="images.spacer" border="0" width="4"/></td>
            <td valign="top">
              <p CLASS="MedBlackText">
                The <bean:message key="global.title"/> provides a reliable method to track and analyze use of Elsevier Engineering Information products within your organization.
                The <bean:message key="global.title"/> features:<br>
                <ul>
                  <li>A fast, streamlined interface.</li>
                  <li>Daily updates.</li>
                  <li>Data downloads in CSV, Excel or XML formats.</li>
                </ul>
              </p>
            </td>
          </tr>
        </table>
        <!-- end of right side table for content -->
      </td>
      <td valign="top" width="200">
         <br/>
        <table border="0" width="200" cellspacing="0" cellpadding="0">
          <tr><td valign="top" width="200" colspan="3" bgcolor="#00449E">&nbsp;&nbsp;<span class="LgWhiteText">Log In</span></td></tr>
          <tr>
            <td valign="top" width="2" bgcolor="#00449E"><html:img pageKey="images.spacer" border="0" width="2"/></td>
            <td valign="top" width="196" >
              <table border="0" width="196" >
                <html:form action="/login" method="post" focus="username" onsubmit="return validateLoginForm(this);">
                <html:hidden  property="<%= Constants.CMD %>" name="<%= Constants.CMD %>" value="auth"/>
                <tr>
                  <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="4"/></td><td valign="top">
                  <span Class="RedText"><html:errors/></span>
                  </td>
                </tr>
                <tr><td valign="top" colspan="3" height="8"><html:img pageKey="images.spacer" border="0" height="8"/></td></tr>
                <tr>
                  <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="4"/></td>
                  <td valign="top"><span Class="MedBlackText"><bean:message key="label.username"/>: </span><br/><span Class="MedBlackText"><html:text  property="username" size="14"/></span></td>
                  <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="4"/></td>
                </tr>
                <tr>
                  <td valign="top" colspan="3" height="8"><html:img pageKey="images.spacer" border="0" height="8"/></td>
                </tr>
                <tr>
                  <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="4"/></td>
                  <td valign="top"><span Class="MedBlackText"><bean:message key="label.password"/>:</span><br><span Class="MedBlackText"><html:password  property="password" size="14"/></span></td>
                  <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="4"/></td>
                </tr>
                <tr>
                  <td valign="top" colspan="3" height="8"><html:img pageKey="images.spacer" border="0" height="8"/></td>
                </tr>
                <tr>
                    <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="4"/></td>
                    <td valign="top">
                      <html:submit/>
                    </td>
                    <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="2"/></td>
                </tr>
                <tr>
                  <td valign="top" colspan="3" height="8"><html:img pageKey="images.spacer" border="0" height="8"/></td>
                </tr>
                <tr>
                  <td valign="top" width="2"><html:img pageKey="images.spacer" border="0" width="4"/></td><td valign="top"><A CLASS="MedBlackText">*Forgot your password?</a><A HREF="http://www.ei.org/contact" CLASS="LgBlueLink">Send us a request</a>  <A CLASS="MedBlackText"> and we will e-mail you your details. </a></td>
                </tr>
                </html:form>
              </table>
            </td>
            <td valign="top" width="2" bgcolor="#00449E"><html:img pageKey="images.spacer" border="0" width="2"/></td>
          </tr>
          <tr><td valign="top" width="200" colspan="3" height="2" bgcolor="#00449E"><html:img pageKey="images.spacer" border="0" height="2"/></td></tr>
        </table>
      </td>
    </tr>
  </table>
  <!-- end of the lower area below the navigation bar -->
  <br/>

  <!-- Start of footer-->
  <jsp:include page="/common/footer.jsp" flush="true"/>

  <html:javascript formName="loginForm" dynamicJavascript="true" staticJavascript="false"/>
  <script language="Javascript1.1" src='<html:rewrite page="/staticJavascript.jsp"/>'></script>
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